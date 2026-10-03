package fontys.sem3.likeme.repository.jpa.platform;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.repository.impl.entity.platform.PlatformSettingsEntity;

@Repository
public interface PlatformSettingsRepositoryJPA extends JpaRepository<PlatformSettingsEntity, Long> {
    Optional<PlatformSettingsEntity> findByType(PlatformSettingType type);
}