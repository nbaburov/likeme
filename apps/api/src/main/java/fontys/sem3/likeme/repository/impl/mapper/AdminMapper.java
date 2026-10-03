package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.impl.entity.user.admin.AdminEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminMapper {
    public static AdminEntity mapToEntity(Admin admin) {
        return AdminEntity.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .permissions(admin.getPermissions())
                .profilePhotoPath(admin.getProfilePhotoPath())
                .isActive(admin.getIsActive())
                .createdOn(admin.getCreatedOn() != null ? admin.getCreatedOn() : new Date())
                .updatedOn(new Date())
                .lastLoginOn(admin.getLastLoginOn())
                .password(admin.getPassword())
                .salt(admin.getSalt())
                .build();
    }

    public static Admin mapToDomain(AdminEntity entity) {
        return Admin.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .role(Role.ADMIN)
                .permissions(entity.getPermissions())
                .profilePhotoPath(entity.getProfilePhotoPath())
                .isActive(entity.getIsActive())
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .lastLoginOn(entity.getLastLoginOn())
                .password(entity.getPassword())
                .salt(entity.getSalt())
                .build();
    }
}
