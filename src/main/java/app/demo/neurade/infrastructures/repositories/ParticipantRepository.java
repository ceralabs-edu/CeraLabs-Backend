package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.ClassParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ClassParticipant, UUID> {
}
