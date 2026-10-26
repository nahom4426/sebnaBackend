package Medco.PremuimFinancing.Repository.User;

import Medco.PremuimFinancing.Entity.User.UserEntity;
import Medco.PremuimFinancing.Entity.User.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<UserFcmToken,Long> {
    UserFcmToken findByUser(UserEntity user);

    UserFcmToken findByUserUserUuid(String userUuid);
}
