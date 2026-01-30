package app.demo.neurade.domain.models.chatbot;

import app.demo.neurade.domain.models.AIPackageInstance;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "conversations",
        indexes = {
                @Index(name = "idx_conversations_instance_id", columnList = "instance_id")
        }
)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "instance_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_conversation_ai_instance")
    )
    private AIPackageInstance instance;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
