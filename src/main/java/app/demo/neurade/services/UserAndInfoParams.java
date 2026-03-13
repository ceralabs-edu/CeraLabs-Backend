package app.demo.neurade.services;

import app.demo.neurade.domain.models.Commune;
import app.demo.neurade.domain.models.Province;
import app.demo.neurade.domain.models.Role;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserAndInfoParams {
    private String email;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
    private String avatarImage;
    private Province city;
    private Commune commune;
    private String bio;
    private String school;
    private String grade;
    private String addressDetail;
    private LocalDate dateOfBirth;
    private List<String> favoriteSubjects;
    private Boolean verified;
}




