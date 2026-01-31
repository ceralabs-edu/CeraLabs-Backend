package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "class_participants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_class_user",
                columnNames = {"class_id", "user_id"}
        ),
        indexes = {
                @Index(
                        name = "idx_class_participant_class",
                        columnList = "class_id"
                ),
                @Index(
                        name = "idx_class_participant_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "class_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_class_participant_class")
    )
    private Classroom clazz;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_class_participant_user")
    )
    private User user;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}
