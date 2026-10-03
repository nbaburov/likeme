package fontys.sem3.likeme.controller.dto.platform;

import java.util.Date;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformSettingsResponse {
    private Long id;
    private PlatformSettingType type;
    private Object value;
    private Date updatedOn;
    private String updatedBy;
}