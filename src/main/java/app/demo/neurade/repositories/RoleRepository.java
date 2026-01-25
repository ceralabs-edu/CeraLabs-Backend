package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {
    Optional<Role> findById(Short id);

}
