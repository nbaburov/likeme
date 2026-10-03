package fontys.sem3.likeme.controller.dto.admin;

import fontys.sem3.likeme.domain.user.admin.Permissions;
import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class AdminResponse {
    private Long id;
    private String username;
    private String email;
    private Permissions permissions;
    private String profilePhotoPath;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
    private Date lastLoginOn;
}