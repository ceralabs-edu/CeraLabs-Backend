package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.assignment.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
}
