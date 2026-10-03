package fontys.sem3.likeme.business.validator.platform;

import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.exception.platform.InvalidPlatformSettingsException;
import fontys.sem3.likeme.domain.platform.PlatformSettingType;

import java.util.Objects;

@Component
public class PlatformSettingsValidator {
    public void validateSettingValue(PlatformSettingType type, String value) {
        if (Objects.requireNonNull(type) == PlatformSettingType.COMMISSION_PERCENTAGE) {
            validateCommissionPercentage(value);
        } else {
            throw new InvalidPlatformSettingsException("Unsupported setting type: " + type);
        }
    }

    private void validateCommissionPercentage(String value) {
        try {
            Double percentage = Double.parseDouble(value);
            if (percentage < 0 || percentage > 100) {
                throw new InvalidPlatformSettingsException("Commission percentage must be between 0 and 100");
            }
        } catch (NumberFormatException e) {
            throw new InvalidPlatformSettingsException("Invalid commission percentage format");
        }
    }
}
