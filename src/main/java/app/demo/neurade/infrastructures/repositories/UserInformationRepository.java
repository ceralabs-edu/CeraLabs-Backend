package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    Optional<UserInformation> findByUser_Id(Long id);

    @Query("""
        select ui from UserInformation ui
        join fetch ui.user
        """)
    List<UserInformation> findInfoByAllUser();
}
