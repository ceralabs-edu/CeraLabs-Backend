package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<Classroom, Long> {
}
