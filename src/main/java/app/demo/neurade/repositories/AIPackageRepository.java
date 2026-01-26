package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.AIPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AIPackageRepository extends JpaRepository<AIPackage, Integer> {
}
