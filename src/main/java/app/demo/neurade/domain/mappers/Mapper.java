package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.UserInfoDTO;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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
}
