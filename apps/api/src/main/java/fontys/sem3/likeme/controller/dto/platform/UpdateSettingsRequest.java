package fontys.sem3.likeme.controller.dto.platform;

import java.util.List;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSettingsRequest {
    @NotEmpty(message = "Settings cannot be empty")
    private List<@Valid SettingValue> settings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SettingValue {
        private PlatformSettingType type;
        private String value;
    }
}