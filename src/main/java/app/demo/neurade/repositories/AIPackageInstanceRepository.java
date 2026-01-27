package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.AIPackageInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AIPackageInstanceRepository extends JpaRepository<AIPackageInstance, UUID> {
    int deleteAIPackageInstanceByClassRoomId(Long classId);
}
