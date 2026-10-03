package fontys.sem3.likeme.domain.platform;

import java.util.Date;

import fontys.sem3.likeme.domain.user.admin.Admin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSettings {
    @Setter(AccessLevel.NONE)
    private Long id;
    private PlatformSettingType type;
    private String value;
    private Date updatedOn;
    private Admin updatedBy;
}