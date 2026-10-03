package fontys.sem3.likeme.business.interfaces.platform;

import java.util.List;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.platform.PlatformSettings;

public interface PlatformSettingsService {
    List<PlatformSettings> getAllSettings();

    PlatformSettings getSettingByType(PlatformSettingType type);

    PlatformSettings updateSetting(Long adminId, PlatformSettingType type, String value);
}