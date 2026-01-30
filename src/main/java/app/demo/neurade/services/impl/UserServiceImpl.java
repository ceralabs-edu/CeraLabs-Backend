package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.UserAndInfoDTO;
import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.RoleType;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.infrastructures.repositories.PeopleManagementRepository;
import app.demo.neurade.infrastructures.repositories.UserInformationRepository;
import app.demo.neurade.infrastructures.repositories.UserRepository;
import app.demo.neurade.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository infoRepository;
    private final PeopleManagementRepository peopleManagementRepository;
    private final Mapper mapper;

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

    public List<User> getUsersUnderManagementByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return getUsersUnderManagement(user);
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
        List<UserAndInfoDTO> result = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserInformation info = infoRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User information not found for user id: " + user.getId()));
            result.add(mapper.toDto(user, info));
        }
        return result;
    }
}
