package app.demo.neurade.domain.models;

import app.demo.neurade.misc.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "user_information",
        indexes = {
                @Index(name = "idx_user_information_user_id", columnList = "user_id"),
                @Index(name = "idx_user_information_city_code", columnList = "city_code"),
                @Index(name = "idx_user_information_sub_district_code", columnList = "sub_district_code")
        }
)
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_user_information_user")
    )
    private User user;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "city_code",
            nullable = false,
            referencedColumnName = "code",
            foreignKey = @ForeignKey(name = "fk_user_information_city")
    )
    private Province city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sub_district_code",
            referencedColumnName = "code",
            foreignKey = @ForeignKey(name = "fk_user_information_sub_district")
    )
    private Commune subDistrict;

    @Column(name = "address_detail", length = 500)
    private String addressDetail;

    @Column(name = "school")
    private String school;

    @Column(name = "grade", length = 50)
    private String grade;

    @Column(name = "favorite_subjects")
    @Convert(converter = StringListConverter.class)
    private List<String> favoriteSubjects;

    @Column(name = "bio")
    private String bio;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "avatar_image", length = 500)
    private String avatarImage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
