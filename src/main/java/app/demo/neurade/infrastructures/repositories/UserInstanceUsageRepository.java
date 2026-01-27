package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.UserAIInstanceUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInstanceUsageRepository extends JpaRepository<UserAIInstanceUsage, Long> {
}
