package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ai_packages")
public class AIPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "api_keys", nullable = false, length = 2000)
    private List<String> apiKeys;

    @Column(name = "total_token", nullable = false)
    private Long totalToken;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "token_rate_limit", nullable = false)
    private Long tokenRateLimit;

    @Column(name = "duration_days", nullable = false)
    private Integer durationInDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
