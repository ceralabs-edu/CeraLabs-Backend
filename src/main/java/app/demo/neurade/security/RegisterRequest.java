package app.demo.neurade.security;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;

//    @Builder.Default
//    private Short roleId = 1;
//
//    @Builder.Default
//    private Boolean status = false;

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

