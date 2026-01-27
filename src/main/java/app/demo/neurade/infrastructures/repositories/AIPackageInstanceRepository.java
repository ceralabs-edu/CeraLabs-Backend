package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.AIPackageInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AIPackageInstanceRepository extends JpaRepository<AIPackageInstance, UUID> {
    @Modifying
    @Query("""
        delete from AIPackageInstance a
        where a.classRoom.id = :classId
          and a.buyer is null
    """)
    int deleteClassroomInstance(@Param("classId") Long classId);

    @Modifying
    @Query("""
        delete from AIPackageInstance a
        where a.buyer.id = :buyerId
          and a.classRoom is null
    """)
    int deletePersonalInstance(@Param("buyerId") Long buyerId);

}
