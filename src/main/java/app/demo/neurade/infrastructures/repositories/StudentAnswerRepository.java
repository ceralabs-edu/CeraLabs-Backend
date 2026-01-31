package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, UUID> {
    List<StudentAnswer> findAllByStudent_IdAndQuestion_Assignment_Id(Long studentId, UUID assignmentId);
    Optional<StudentAnswer> findByStudent_IdAndQuestion_Id(Long studentId, UUID questionId);
}
