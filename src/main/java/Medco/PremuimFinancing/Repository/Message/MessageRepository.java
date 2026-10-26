package Medco.PremuimFinancing.Repository.Message;

import Medco.PremuimFinancing.Entity.Message.Message;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Enums.Message.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderOrReceiverOrderByCreatedAtDesc(UserEntity sender, UserEntity receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2) OR (m.sender = :u2 AND m.receiver = :u1) ORDER BY m.createdAt DESC")
    Page<Message> findThread(@Param("u1") UserEntity u1, @Param("u2") UserEntity u2, Pageable pageable);

    @Query("SELECT MAX(m.createdAt) FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2) OR (m.sender = :u2 AND m.receiver = :u1)")
    Instant findLastMessageAtBetween(@Param("u1") UserEntity u1, @Param("u2") UserEntity u2);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.createdAt BETWEEN :start AND :end")
    long countCreatedBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
            SELECT m.type, COUNT(m)
            FROM Message m
            WHERE m.createdAt BETWEEN :start AND :end
            GROUP BY m.type
            """)
    List<Object[]> countByTypeBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);

    long countByReceiverAndReadAtIsNull(UserEntity receiver);

    @Query("SELECT m.sender.userUuid, COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.readAt IS NULL GROUP BY m.sender.userUuid")
    List<Object[]> countUnreadBySenderForReceiver(@Param("receiver") UserEntity receiver);

    @Query("UPDATE Message m SET m.readAt = :readAt WHERE m.receiver = :receiver AND m.sender = :sender AND m.readAt IS NULL")
    @org.springframework.data.jpa.repository.Modifying
    int markThreadAsRead(@Param("receiver") UserEntity receiver, @Param("sender") UserEntity sender, @Param("readAt") Instant readAt);
}
