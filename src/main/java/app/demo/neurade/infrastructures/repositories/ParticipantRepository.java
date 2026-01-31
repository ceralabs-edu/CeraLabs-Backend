package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.ClassParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ClassParticipant, UUID> {
    List<ClassParticipant> findAllByClazz_Id(Long clazzId);
    boolean existsByClazz_IdAndUser_Id(Long clazzId, Long userId);
}
