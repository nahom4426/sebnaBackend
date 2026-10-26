package Medco.PremuimFinancing.ServiceImpl.Like;

import Medco.PremuimFinancing.Entity.Post.Post;
import Medco.PremuimFinancing.Entity.Post.PostLike;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Repository.Post.PostLikeRepository;
import Medco.PremuimFinancing.Repository.Post.PostRepository;
import Medco.PremuimFinancing.Repository.User.UserRepository;
import Medco.PremuimFinancing.Service.Like.PostLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostLikeServiceImpl(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PostLike like(UUID postId, String userUuid) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            return null; // already liked
        }
        PostLike like = new PostLike();
        like.setPost(post);
        like.setActorId("user:" + userUuid);
        like.setUser(user);
        return postLikeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlike(UUID postId, String userUuid) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();

        postLikeRepository.findByPostAndUser(post, user)
                .ifPresent(postLikeRepository::delete);
    }

    @Override
    public long count(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        return postLikeRepository.countByPost(post);
    }

    @Override
    public boolean likedByUser(UUID postId, String userUuid) {
        Post post = postRepository.findById(postId).orElseThrow();
        UserEntity user = userRepository.findByUserUuid(userUuid).orElseThrow();
        return postLikeRepository.existsByPostAndUser(post, user);
    }
}
