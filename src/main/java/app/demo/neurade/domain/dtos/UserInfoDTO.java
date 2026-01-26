package app.demo.neurade.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class UserInfoDTO {
    private String firstName;
    private String lastName;
    private String cityCode;
    private String subDistrictCode;
    private String addressDetail;
    private String school;
    private String grade;
    private List<String> favoriteSubjects;
    private String bio;
    private LocalDate dateOfBirth;
    private String avatarUrl;
}
