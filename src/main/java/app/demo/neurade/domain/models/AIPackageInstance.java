package app.demo.neurade.domain.models;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ai_package_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ai_package_instance_ai_package")
    )
    private AIPackage aiPackage;
}
