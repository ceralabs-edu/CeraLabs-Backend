package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.ClassParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ClassParticipant, UUID> {

    @Query("""
        select p from ClassParticipant p
        join fetch p.user u
        join fetch u.role
        where p.clazz.id = :clazzId
    """)
    List<ClassParticipant> findAllByClazz_Id(Long clazzId);
    boolean existsByClazz_IdAndUser_Id(Long clazzId, Long userId);

    @Query("""
        select p from ClassParticipant p
        where p.clazz.id = :clazzId
        and p.user.id in :userIds
    """)
    Set<Long> findExistingUserIdsInClass(Long clazzId, List<Long> userIds);
}
