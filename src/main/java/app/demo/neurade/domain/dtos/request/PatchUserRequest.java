package app.demo.neurade.domain.dtos.request;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserRequest {
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
