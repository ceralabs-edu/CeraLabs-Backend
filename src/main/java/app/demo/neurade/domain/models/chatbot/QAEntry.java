package app.demo.neurade.domain.models.chatbot;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "qa_entries",
        indexes = {
                @Index(name = "idx_qa_conversation_created", columnList = "conversation_id, created_at desc")
        }
)
public class QAEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Conversation conversation;

    @Column(columnDefinition = "TEXT", name = "question_text")
    private String questionText; // có thể null

    @Column(columnDefinition = "TEXT")
    private String answer;

    @OneToMany(
            mappedBy = "qaEntry",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("orderIndex ASC")
    private List<QuestionAsset> assets;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

