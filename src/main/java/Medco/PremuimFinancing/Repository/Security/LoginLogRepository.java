package Medco.PremuimFinancing.Repository.Security;

import Medco.PremuimFinancing.Entity.Security.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

public interface LoginLogRepository extends JpaRepository<LoginLog, UUID> {

    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.createdAt BETWEEN :start AND :end")
    long countCreatedBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.success = :success AND l.createdAt BETWEEN :start AND :end")
    long countSuccessBetween(@Param("success") boolean success, @Param("start") Instant start, @Param("end") Instant end);

    @Modifying
    @Transactional
    @Query("DELETE FROM LoginLog l WHERE l.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}
