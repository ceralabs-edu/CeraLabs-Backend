package app.demo.neurade.security;

import app.demo.neurade.domain.models.RoleType;
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

    @Builder.Default
    private short roleId = RoleType.STUDENT.getRoleId();

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

