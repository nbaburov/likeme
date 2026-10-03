package fontys.sem3.likeme.business.converter.user.admin;

import java.util.Date;

import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminConversionException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminDataException;
import fontys.sem3.likeme.controller.dto.admin.AdminResponse;
import fontys.sem3.likeme.controller.dto.admin.CreateAdminRequest;
import fontys.sem3.likeme.controller.dto.admin.UpdateAdminRequest;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.admin.Admin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminConverter {
        public static Admin requestToDomain(CreateAdminRequest request) {
                if (request == null) {
                        throw new InvalidRequest("Admin request cannot be null");
                }

                try {
                        return Admin.builder()
                                        .username(request.getUsername())
                                        .email(request.getEmail())
                                        .permissions(request.getPermissions())
                                        .profilePhotoPath(request.getProfilePhotoPath())
                                        .password(request.getPassword())
                                        .role(Role.ADMIN)
                                        .isActive(true)
                                        .createdOn(new Date())
                                        .updatedOn(new Date())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidAdminConversionException("Failed to create admin: " + e.getMessage(), e);
                }
        }

        public static Admin requestToDomain(UpdateAdminRequest request, Long id, Admin existingAdmin) {
                if (request == null || existingAdmin == null || id == null) {
                        throw new InvalidRequest("Update request, existing admin or ID cannot be null");
                }

                try {
                        return Admin.builder()
                                        .id(id)
                                        .username(request.getUsername() != null ? request.getUsername()
                                                        : existingAdmin.getUsername())
                                        .email(request.getEmail() != null ? request.getEmail()
                                                        : existingAdmin.getEmail())
                                        .permissions(
                                                        request.getPermissions() != null ? request.getPermissions()
                                                                        : existingAdmin.getPermissions())
                                        .password(request.getPassword() != null ? request.getPassword()
                                                        : existingAdmin.getPassword())
                                        .salt(existingAdmin.getSalt())
                                        .profilePhotoPath(request.getProfilePhotoPath() != null
                                                        ? request.getProfilePhotoPath()
                                                        : existingAdmin.getProfilePhotoPath())
                                        .isActive(request.getIsActive() != null ? request.getIsActive()
                                                        : existingAdmin.getIsActive())
                                        .role(existingAdmin.getRole())
                                        .createdOn(existingAdmin.getCreatedOn())
                                        .lastLoginOn(existingAdmin.getLastLoginOn())
                                        .updatedOn(new Date())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidAdminConversionException("Failed to update admin: " + e.getMessage());
                }
        }

        public static AdminResponse domainToResponse(Admin admin) {
                if (admin == null) {
                        throw new InvalidAdminDataException("Admin cannot be null");
                }

                try {
                        return AdminResponse.builder()
                                        .id(admin.getId())
                                        .username(admin.getUsername())
                                        .email(admin.getEmail())
                                        .permissions(admin.getPermissions())
                                        .profilePhotoPath(admin.getProfilePhotoPath())
                                        .isActive(admin.getIsActive())
                                        .createdOn(admin.getCreatedOn())
                                        .updatedOn(admin.getUpdatedOn())
                                        .lastLoginOn(admin.getLastLoginOn())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidAdminConversionException("Failed to convert admin to response: " + e.getMessage());
                }
        }
}
