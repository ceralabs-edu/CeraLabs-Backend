package app.demo.neurade.services.impl;

import app.demo.neurade.configs.RabbitMQConfig;
import app.demo.neurade.domain.dtos.UserAndInfoDTO;
import app.demo.neurade.domain.dtos.messages.UserCreatedMessage;
import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.RoleType;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.domain.models.Role;
import app.demo.neurade.domain.models.Province;
import app.demo.neurade.domain.models.Commune;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.infrastructures.repositories.PeopleManagementRepository;
import app.demo.neurade.infrastructures.repositories.UserInformationRepository;
import app.demo.neurade.infrastructures.repositories.UserRepository;
import app.demo.neurade.infrastructures.repositories.RoleRepository;
import app.demo.neurade.infrastructures.repositories.ProvinceRepository;
import app.demo.neurade.infrastructures.repositories.CommuneRepository;
import app.demo.neurade.security.RegisterRequest;
import app.demo.neurade.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import app.demo.neurade.domain.dtos.messages.MessageStatus;
import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository infoRepository;
    private final PeopleManagementRepository peopleManagementRepository;
    private final RoleRepository roleRepository;
    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final Mapper mapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public UserInformation updateUserInfo(User currentUser, String email, PatchUserRequest req) {
        if (!currentUser.getRole().isRoleType(RoleType.ADMIN) && !currentUser.getEmail().equals(email)) {
            throw new UnauthorizedException("You are not authorized to update this user's information");
        }
        UserInformation info = infoRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User information not found"));

        BeanUtils.copyProperties(req, info, getNullPropertyNames(req));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return infoRepository.save(info);
    }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    @Override
    public List<User> getUsersUnderManagement(User user) {
        List<User> users = new ArrayList<>();
        if (user.getRole().isRoleType(RoleType.ADMIN)) {
            users = userRepository.findAll();
            users.remove(user);
        } else if (
                user.getRole().isRoleType(RoleType.ORGANIZATION) ||
            user.getRole().isRoleType(RoleType.TEACHER)
        ) {
            users = peopleManagementRepository.findManagedUsersByManagerId(user.getId());
        }
        return users;
    }

    @Override
    public UserAndInfoDTO getUserAndInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        UserInformation info = infoRepository.findByUser_Id(id)
                .orElseThrow(() -> new EntityNotFoundException("User information not found for user id: " + id));
        return mapper.toDto(user, info);
    }

    @Override
    public List<UserAndInfoDTO> getAllUsersAndInfo() {
        List<UserInformation> infos = infoRepository.findInfoByAllUser();
        return infos.stream()
                .map(info -> {
                    User user = info.getUser();
                    return mapper.toDto(user, info);
                })
                .toList();
    }

    @Override
    @Transactional
    public User createUserWithInformation(RegisterRequest req) {
        // Check if user already exists
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            log.warn("{} is already registered", req.getEmail());
            throw new IllegalArgumentException("User already exists with email: " + req.getEmail());
        }

        Role role = roleRepository.findById(req.getRoleId())
                .orElseThrow(() -> {
                    log.warn("Role with id {} not found", req.getRoleId());
                    return new IllegalArgumentException("Role not found with id: " + req.getRoleId());
                });

        // Create new user
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();

        user = userRepository.save(user);

        // Find province and commune
        Province city = provinceRepository.findByCode(req.getCityCode())
                .orElseThrow(() -> {
                    log.warn("City code {} cannot be found", req.getCityCode());
                    return new IllegalArgumentException("Province not found with code: " + req.getCityCode());
                });
        Commune commune = communeRepository.findByCode(req.getSubDistrictCode())
                .orElseThrow(() -> {
                    log.warn("Sub district code {} cannot be found", req.getSubDistrictCode());
                    return new IllegalArgumentException("Commune not found with code: " + req.getSubDistrictCode());
                });

        // Create new user information
        UserInformation userInfo = UserInformation.builder()
                .user(user)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .city(city)
                .subDistrict(commune)
                .bio(req.getBio())
                .school(req.getSchool())
                .grade(req.getGrade())
                .addressDetail(req.getAddressDetail())
                .dateOfBirth(req.getDateOfBirth())
                .favoriteSubjects(req.getFavoriteSubjects())
                .build();

        infoRepository.save(userInfo);

        log.info("User {} created successfully with full information", user.getEmail());

        // Publish user created message to RabbitMQ
        publishUserCreatedMessage(user, UserCreatedMessage.UserCreationSource.REGISTRATION);

        return user;
    }

    @Override
    @Transactional
    public User createUserFromOAuth(String email, OAuth2User oauth2User) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("User {} already exists", email);
            return userRepository.findByEmail(email).get();
        }

        Role roleRef = entityManager.getReference(Role.class, RoleType.STUDENT.getRoleId());

        User newUser = User.builder()
                .email(email)
                .password("")
                .role(roleRef)
                .verified(oauth2User.getAttribute("email_verified"))
                .build();

        UserInformation info = UserInformation.builder()
                .user(newUser)
                .firstName(oauth2User.getAttribute("given_name"))
                .lastName(oauth2User.getAttribute("family_name"))
                .avatarImage(oauth2User.getAttribute("picture"))
                .build();

        userRepository.save(newUser);
        infoRepository.save(info);

        log.info("User {} registered successfully via OAuth", email);

        // Publish user created message to RabbitMQ
        publishUserCreatedMessage(newUser, UserCreatedMessage.UserCreationSource.OAUTH);

        return newUser;
    }

    /**
     * Helper method to publish user created message to RabbitMQ
     */
    private void publishUserCreatedMessage(User user, UserCreatedMessage.UserCreationSource source) {
        try {
            UserCreatedMessage message = UserCreatedMessage.builder()
                    .id(UUID.randomUUID())
                    .status(MessageStatus.QUEUED)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .roleId(user.getRole().getId())
                    .source(source)
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.USER_CREATED_EXCHANGE,
                    RabbitMQConfig.USER_CREATED_ROUTING_KEY,
                    message
            );

            log.info("Published user created message for user: {} to RabbitMQ", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to publish user created message for user: {}", user.getEmail(), e);
            // Don't throw exception - user creation should not fail if message publishing fails
        }
    }
}
