package Medco.PremuimFinancing.Repository.Post;

import Medco.PremuimFinancing.Entity.Post.Comment;
import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @EntityGraph(attributePaths = {"user", "post"})
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    
    Page<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);
    
    long countByPost(Post post);
    
    List<Comment> findByUserOrderByCreatedAtDesc(UserEntity user);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.createdAt BETWEEN :start AND :end")
    long countCreatedBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);
}
