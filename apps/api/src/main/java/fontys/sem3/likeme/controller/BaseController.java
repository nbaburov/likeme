package fontys.sem3.likeme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import fontys.sem3.likeme.business.exception.security.auth.UnauthorizedException;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.admin.Permissions;

public abstract class BaseController {

    @Autowired
    private AdminService adminService;

    protected AccessToken getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getDetails() == null) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return (AccessToken) authentication.getDetails();
    }

    protected Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    protected String getCurrentUserRole() {
        return getCurrentUser().getRole().name();
    }

    protected void validateOwnership(Long resourceUserId) {
        AccessToken currentUser = getCurrentUser();

        // Allow access if user is the owner
        if (currentUser.getUserId().equals(resourceUserId)) {
            return;
        }

        // Check if user is admin with FULL permissions
        if (Role.ADMIN.equals(currentUser.getRole())) {
            try {
                Admin admin = adminService.getAdminById(currentUser.getUserId());
                if (Permissions.FULL.equals(admin.getPermissions())) {
                    return;
                }
            } catch (Exception ignored) {
                // If admin not found or other error, continue to unauthorized
            }
        }

        throw new UnauthorizedException("User is not authorized to access this resource");
    }

    protected boolean isFullAdmin() {
        AccessToken currentUser = getCurrentUser();
        if (!Role.ADMIN.equals(currentUser.getRole())) {
            return false;
        }

        try {
            Admin admin = adminService.getAdminById(currentUser.getUserId());
            return Permissions.FULL.equals(admin.getPermissions());
        } catch (Exception e) {
            return false;
        }
    }
}