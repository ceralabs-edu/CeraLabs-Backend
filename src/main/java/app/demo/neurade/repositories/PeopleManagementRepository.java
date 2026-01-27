package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.PeopleManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeopleManagementRepository extends JpaRepository<PeopleManagement, Long> {
    boolean existsByManagerIdAndManagedId(
            Long managerId,
            Long managedId
    );
}
