package app.demo.neurade.domain.models.assignment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assignment_questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AssignmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * question_1, question_2...
     */
    @Column(name = "question_key", nullable = false)
    private String questionKey;

    /**
     * MCQ | TF | SHORT_ANSWER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    /**
     * URL ảnh câu hỏi (MinIO)
     */
    @Column(name = "question_image_url", nullable = false, columnDefinition = "TEXT")
    private String questionImageUrl;

    /**
     * URL ảnh các phương án trả lời (MinIO)
     */
    @Column(name = "answer_image_urls")
    private List<String> answerImageUrls;

    /**
     * A / B / C / D / Đ / S / null
     */
    @Column(name = "correct_answer")
    private String correctAnswer;

    /**
     * Ảnh giải thích (nếu có)
     */
    @Column(name = "explain_image_url", columnDefinition = "TEXT")
    private String explainImageUrl;

    /**
     * Trang PDF
     */
    @Column(name = "page")
    private Integer page;

    @ManyToOne
    @JoinColumn(
            name = "assignment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_assignment_question_assignment")
    )
    private Assignment assignment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
