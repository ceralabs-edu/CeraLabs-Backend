package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByCreatorIn(List<User> users);
}
