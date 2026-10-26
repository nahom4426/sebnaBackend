package Medco.PremuimFinancing.Entity.Institution;

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
@Table(name = "user_institution", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
public class UserInstitution extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
}
