package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestion, UUID> {
    List<AssignmentQuestion> findAllByAssignment(Assignment assignment);
}
