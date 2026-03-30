package app.demo.neurade.domain.models.chatbot;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "question_assets",
        indexes = {
                @Index(name = "idx_question_asset_qa_entry", columnList = "qa_entry_id")
        }
)
public class QuestionAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "qa_entry_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_question_asset_qa_entry", foreignKeyDefinition = "FOREIGN KEY (qa_entry_id) REFERENCES qa_entries(id) ON DELETE CASCADE ON UPDATE CASCADE")
    )
    private QAEntry qaEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AssetType type; // IMAGE, PDF

    @Column(name = "object_url", nullable = false)
    private String objectUrl;   // MinIO url
    @Column(name = "mime_type", nullable = false)
    private String mimeType;    // image/png
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex; // thứ tự ảnh
}
