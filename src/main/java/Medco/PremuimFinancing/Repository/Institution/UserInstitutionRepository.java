package Medco.PremuimFinancing.Repository.Institution;

import Medco.PremuimFinancing.Entity.Institution.UserInstitution;
import Medco.PremuimFinancing.Entity.Institution.Institution;
import Medco.PremuimFinancing.Entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInstitutionRepository extends JpaRepository<UserInstitution, UUID> {
    Optional<UserInstitution> findByUser(UserEntity user);
    Optional<UserInstitution> findByUser_UserUuid(String userUuid);
    List<UserInstitution> findByInstitution(Institution institution);
}
