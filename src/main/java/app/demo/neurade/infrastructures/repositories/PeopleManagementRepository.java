package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.PeopleManagement;
import app.demo.neurade.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeopleManagementRepository extends JpaRepository<PeopleManagement, Long> {
    boolean existsByManagerIdAndManagedId(
            Long managerId,
            Long managedId
    );

    @Query("""
        select pm.managed
        from PeopleManagement pm
        where pm.manager.id = :managerId
    """)
    List<User> findManagedUsersByManagerId(@Param("managerId") Long managerId);
}
