package app.demo.neurade.security;

import app.demo.neurade.domain.models.Commune;
import app.demo.neurade.domain.models.Province;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.repositories.CommuneRepository;
import app.demo.neurade.repositories.ProvinceRepository;
import app.demo.neurade.repositories.UserInformationRepository;
import app.demo.neurade.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        // Check if user already exists
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            log.warn(req.getEmail() + " is already registered");
            throw new IllegalArgumentException("User already exists with email: " + req.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roleId(req.getRoleId())
                .build();

        user = userRepository.save(user);

        // Find province and commune
        Province city = provinceRepository.findByCode(req.getCityCode())
                .orElseThrow(() -> {
                    log.warn("City code " + req.getCityCode() + " cannot be found");
                    return new IllegalArgumentException("Province not found with id: " + req.getCityCode());
                });
        Commune commune = communeRepository.findByCode(req.getSubDistrictCode())
                .orElseThrow(() -> {
                    log.warn("Sub district code " + req.getSubDistrictCode() + " cannot be found");
                    return new IllegalArgumentException("Commune not found with id: " + req.getSubDistrictCode());
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
                .avatarImage(req.getAvatarUrl())
                .build();

        userInformationRepository.save(userInfo);

        return user;
    }
}
