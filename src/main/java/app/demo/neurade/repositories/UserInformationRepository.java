package app.demo.neurade.repositories;

import app.demo.neurade.domain.models.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
}
