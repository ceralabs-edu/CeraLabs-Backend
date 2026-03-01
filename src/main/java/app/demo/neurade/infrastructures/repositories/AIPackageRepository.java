package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.AIPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AIPackageRepository extends JpaRepository<AIPackage, Integer> {
    Optional<AIPackage> findByName(String name);
}
