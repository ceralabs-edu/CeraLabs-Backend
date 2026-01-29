package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    Optional<Province> findByCode(String code);
}
