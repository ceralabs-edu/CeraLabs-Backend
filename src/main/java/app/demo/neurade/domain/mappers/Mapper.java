package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.ClassDTO;
import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.UserInfoDTO;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .verified(user.getVerified())
                .build();
    }

    public UserInfoDTO toDto(UserInformation userInformation) {
        return UserInfoDTO.builder()
                .firstName(userInformation.getFirstName())
                .lastName(userInformation.getLastName())
                .cityCode(userInformation.getCity().getCode())
                .subDistrictCode(userInformation.getSubDistrict().getCode())
                .addressDetail(userInformation.getAddressDetail())
                .school(userInformation.getSchool())
                .grade(userInformation.getGrade())
                .bio(userInformation.getBio())
                .favoriteSubjects(userInformation.getFavoriteSubjects())
                .dateOfBirth(userInformation.getDateOfBirth())
                .avatarUrl(userInformation.getAvatarImage())
                .build();
    }

    public ClassDTO toDto(Classroom classroom) {
        return ClassDTO.builder()
                .name(classroom.getName())
                .creatorId(classroom.getCreator().getId())
                .build();
    }
}
