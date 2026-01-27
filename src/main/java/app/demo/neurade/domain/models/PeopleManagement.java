package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "people_management",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_manager_managed",
                        columnNames = {"manager_id", "managed_id"}
                )
        },
        indexes = {
                @Index(name = "idx_manager", columnList = "manager_id"),
                @Index(name = "idx_managed", columnList = "managed_id")
        }
)
public class PeopleManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "manager_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_people_management_manager")
    )
    private User manager;

    @ManyToOne
    @JoinColumn(
            name = "managed_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_people_management_managed")
    )
    private User managed;
}
