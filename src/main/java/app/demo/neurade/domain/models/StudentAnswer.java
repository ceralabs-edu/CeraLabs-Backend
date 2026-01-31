package app.demo.neurade.domain.models;

import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "student_answers",
        indexes = {
                @Index(
                        name = "idx_student_answers_student_id",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_student_answers_question_id",
                        columnList = "question_id"
                )
        }
)
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_answers_students"
            )
    )
    private User student;

    @ManyToOne
    @JoinColumn(
            name = "question_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_student_answers_assignment_questions"
            )
    )
    private AssignmentQuestion question;

    @Column(name = "judgement", columnDefinition = "TEXT")
    private String judgement;
}
