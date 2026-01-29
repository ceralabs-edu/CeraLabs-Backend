package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserInstanceUsageRepository extends JpaRepository<UserAIInstanceUsage, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select u from UserAIInstanceUsage u
        where u.user = :user and u.instance.id = :instanceId
    """)
    Optional<UserAIInstanceUsage> findForUpdate(
            @Param("user") User user,
            @Param("instanceId") UUID instanceId
    );

}
