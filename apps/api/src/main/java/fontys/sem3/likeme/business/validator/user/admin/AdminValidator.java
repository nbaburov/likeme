package fontys.sem3.likeme.business.validator.user.admin;

import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.exception.user.admin.DuplicateAdminException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminDataException;
import fontys.sem3.likeme.business.converter.user.admin.AdminConverter;
import fontys.sem3.likeme.business.validator.security.PasswordValidator;
import fontys.sem3.likeme.controller.dto.admin.UpdateAdminRequest;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.admin.AdminRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminValidator {
    private final AdminRepository adminRepository;
    private final PasswordValidator passwordValidator;

    public void validateCreate(Admin admin) {

        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new DuplicateAdminException("Username already exists");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new DuplicateAdminException("Email already exists");
        }
    }

    public void validateUpdate(Admin admin) {
        adminRepository.findById(admin.getId()).ifPresent(existingAdmin -> {
            if (!existingAdmin.getUsername().equals(admin.getUsername())
                    && adminRepository.existsByUsername(admin.getUsername())) {
                throw new DuplicateAdminException("Username already exists");
            }
            if (!existingAdmin.getEmail().equals(admin.getEmail())
                    && adminRepository.existsByEmail(admin.getEmail())) {
                throw new DuplicateAdminException("Email already exists");
            }
        });
    }

    public void validateUpdate(UpdateAdminRequest request, Admin existingAdmin) {
        if (request == null || (request.getUsername() == null && request.getEmail() == null
                && request.getPermissions() == null && request.getProfilePhotoPath() == null
                && request.getIsActive() == null && request.getPassword() == null)) {
            throw new InvalidAdminDataException("Update request cannot be empty");
        }

        // Check for no changes
        Admin updatedAdmin = AdminConverter.requestToDomain(request, existingAdmin.getId(), existingAdmin);
        if (existingAdmin.equals(updatedAdmin) && passwordValidator.comparePasswords(request.getPassword(),
                existingAdmin.getPassword(), existingAdmin.getSalt())) {
            throw new InvalidAdminDataException("No changes detected for admin with id: " + existingAdmin.getId());
        }
    }
}