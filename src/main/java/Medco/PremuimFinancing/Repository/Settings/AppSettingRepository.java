package Medco.PremuimFinancing.Repository.Settings;

import Medco.PremuimFinancing.Entity.Settings.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    Optional<AppSetting> findByKey(String key);
}
