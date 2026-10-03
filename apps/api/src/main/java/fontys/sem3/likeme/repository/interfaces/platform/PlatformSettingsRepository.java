package fontys.sem3.likeme.repository.interfaces.platform;

import java.util.List;
import java.util.Optional;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.platform.PlatformSettings;

public interface PlatformSettingsRepository {
    PlatformSettings save(PlatformSettings settings);
    Optional<PlatformSettings> findByType(PlatformSettingType type);
    List<PlatformSettings> findAll();
}