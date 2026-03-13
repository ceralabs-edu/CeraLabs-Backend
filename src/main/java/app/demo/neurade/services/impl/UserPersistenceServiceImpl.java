package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.infrastructures.repositories.UserInformationRepository;
import app.demo.neurade.infrastructures.repositories.UserRepository;
import app.demo.neurade.services.UserAndInfoParams;
import app.demo.neurade.services.UserPersistenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPersistenceServiceImpl implements UserPersistenceService {
    private final UserRepository userRepository;
    private final UserInformationRepository infoRepository;

    @Override
    @Transactional
    public User createUserAndInfo(UserAndInfoParams params) {
        User user = User.builder()
                .email(params.getEmail())
                .password(params.getPassword())
                .role(params.getRole())
                .verified(params.getVerified())
                .build();
        user = userRepository.save(user);

        UserInformation info = UserInformation.builder()
                .user(user)
                .firstName(params.getFirstName())
                .lastName(params.getLastName())
                .avatarImage(params.getAvatarImage())
                .city(params.getCity())
                .subDistrict(params.getCommune())
                .bio(params.getBio())
                .school(params.getSchool())
                .grade(params.getGrade())
                .addressDetail(params.getAddressDetail())
                .dateOfBirth(params.getDateOfBirth())
                .favoriteSubjects(params.getFavoriteSubjects())
                .build();

        infoRepository.save(info);
        return user;
    }
}
