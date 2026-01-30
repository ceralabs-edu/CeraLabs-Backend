package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.Commune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommuneRepository extends JpaRepository<Commune, Integer> {
    Optional<Commune> findByCode(String code);
    @Query("""
        SELECT c FROM Commune c
        JOIN FETCH c.province p
        WHERE p.id = :provinceId
    """)
    List<Commune> findByProvinceId(Integer provinceId);
}
