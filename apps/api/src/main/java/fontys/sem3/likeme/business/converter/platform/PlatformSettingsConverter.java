package fontys.sem3.likeme.business.converter.platform;

import fontys.sem3.likeme.business.exception.platform.PlatformSettingsConversionException;
import fontys.sem3.likeme.controller.dto.platform.PlatformSettingsResponse;
import fontys.sem3.likeme.domain.platform.PlatformSettings;
import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformSettingsConverter {

    public static PlatformSettingsResponse mapToResponse(PlatformSettings settings) {
        if (settings == null) {
            throw new PlatformSettingsConversionException("Platform settings cannot be null");
        }

        try {
            Object convertedValue = convertValueByType(settings.getType(), settings.getValue());

            return PlatformSettingsResponse.builder()
                    .id(settings.getId())
                    .type(settings.getType())
                    .value(convertedValue)
                    .updatedOn(settings.getUpdatedOn())
                    .updatedBy(settings.getUpdatedBy() != null ? settings.getUpdatedBy().getUsername() : null)
                    .build();
        } catch (Exception e) {
            throw new PlatformSettingsConversionException("Failed to convert platform settings to response", e);
        }
    }

    private static Object convertValueByType(PlatformSettingType type, String value) {
        if (value == null)
            return null;

        try {
            return switch (type) {
                case COMMISSION_PERCENTAGE -> Double.parseDouble(value);
            };
        } catch (NumberFormatException e) {
            throw new PlatformSettingsConversionException("Failed to convert value for type " + type, e);
        }
    }
}