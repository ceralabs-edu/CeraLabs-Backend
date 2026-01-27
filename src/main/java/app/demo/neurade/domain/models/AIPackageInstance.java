package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ai_packages_instances")
public class AIPackageInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "ai_package_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ai_package_instance_ai_package")
    )
    private AIPackage aiPackage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "class_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ai_package_instance_class")
    )
    private Classroom classRoom;

    @ManyToOne
    @JoinColumn(
            name = "purchased_by",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ai_package_instance_user")
    )
    private User buyer;

    @Column(name = "remaining_token", nullable = false)
    private Long remainingToken;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
