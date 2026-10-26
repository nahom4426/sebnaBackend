package Medco.PremuimFinancing.Repository.Post;

import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Enums.Post.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByOrderByCreatedAtDesc();

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByCategoryOrderByCreatedAtDesc(PostCategory category, Pageable pageable);
    
    Page<Post> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.createdAt BETWEEN :start AND :end")
    long countCreatedBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT function('date', p.createdAt), COUNT(p) FROM Post p WHERE p.createdAt BETWEEN :start AND :end GROUP BY function('date', p.createdAt) ORDER BY function('date', p.createdAt)")
    List<Object[]> countDailyCreatedBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT p.category, COUNT(p) FROM Post p WHERE p.createdAt BETWEEN :start AND :end GROUP BY p.category")
    List<Object[]> countByCategoryBetweenGlobal(@Param("start") Instant start, @Param("end") Instant end);
}
