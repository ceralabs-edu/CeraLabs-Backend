package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.Commune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommuneRepository extends JpaRepository<Commune, Integer> {
    Optional<Commune> findByCode(String code);
}
