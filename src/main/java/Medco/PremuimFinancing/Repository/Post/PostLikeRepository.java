package Medco.PremuimFinancing.Repository.Post;

import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.Post.PostLike;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    boolean existsByPostAndUser(Post post, UserEntity user);
    boolean existsByPostAndActorId(Post post, String actorId);
    long countByPost(Post post);
    
    Optional<PostLike> findByPostAndUser(Post post, UserEntity user);
    Optional<PostLike> findByPostAndActorId(Post post, String actorId);
    
    Page<PostLike> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);
    
    List<PostLike> findByUserOrderByCreatedAtDesc(UserEntity user);
}
