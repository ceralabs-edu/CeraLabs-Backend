package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.JwtAccessToken;
import app.demo.neurade.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JwtAccessTokenRepository extends JpaRepository<JwtAccessToken, UUID> {
    Optional<JwtAccessToken> findByToken(String token);
    List<JwtAccessToken> findAllByUser(User user);
}
