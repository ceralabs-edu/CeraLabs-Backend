package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
    Optional<UserInformation> findByUserEmail(String email);
}
