package Medco.PremuimFinancing.Entity.Post;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.utils.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "actor_id", nullable = false)
    private String actorId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    @Column(columnDefinition = "text", nullable = false)
    private String comment;
}
