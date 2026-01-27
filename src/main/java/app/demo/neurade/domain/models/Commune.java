package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "communes",
        indexes = {
                @Index(name = "idx_communes_code", columnList = "code"),
                @Index(name = "idx_communes_slug", columnList = "slug"),
                @Index(name = "idx_communes_province_code", columnList = "province_code")
        }
)
public class Commune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "path", nullable = false, length = 500)
    private String path;

    @Column(name = "path_with_type", nullable = false, length = 500)
    private String pathWithType;

    @ManyToOne
    @JoinColumn(
            name = "province_code",
            referencedColumnName = "code",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_commune_province")
    )
    private Province province;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}