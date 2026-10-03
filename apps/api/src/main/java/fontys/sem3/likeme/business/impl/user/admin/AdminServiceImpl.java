package fontys.sem3.likeme.business.impl.user.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.exception.user.admin.AdminServiceException;
import fontys.sem3.likeme.business.exception.user.admin.DuplicateAdminException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminDataException;
import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.admin.AdminValidator;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.admin.AdminRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final AdminValidator adminValidator;
    private final PasswordService passwordService;
    private final FileStorageService fileStorageService;

    @Override
    public List<Admin> getAllAdmins() {
        try {
            return adminRepository.findAll();
        } catch (Exception e) {
            throw new AdminServiceException("Failed to retrieve all admins", e);
        }
    }

    @Override
    public Admin getAdminById(Long id) {
        if (id == null) {
            throw new InvalidAdminDataException("Admin ID cannot be null");
        }
        try {
            return adminRepository.findById(id)
                    .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + id));
        } catch (AdminNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceException("Error retrieving admin with id: " + id, e);
        }
    }

    @Override
    public Admin getAdminByUsername(String username) {
        if (username == null) {
            throw new InvalidAdminDataException("Username cannot be null");
        }

        try {
            return adminRepository.findByUsername(username)
                    .orElseThrow(() -> new AdminNotFoundException("Admin not found with username: " + username));
        } catch (AdminNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceException("Error retrieving admin with username: " + username, e);
        }
    }

    @Transactional
    @Override
    public Admin createAdmin(Admin admin) {
        if (admin == null) {
            throw new InvalidAdminDataException("Admin cannot be null");
        }

        try {
            adminValidator.validateCreate(admin);

            String salt = passwordService.generateSalt();
            admin.setSalt(salt);
            admin.setPassword(passwordService.hashPassword(admin.getPassword(), salt));

            return adminRepository.save(admin);
        } catch (DuplicateAdminException | InvalidAdminDataException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceException("Failed to create admin", e);
        }
    }

    @Transactional
    @Override
    public Admin updateAdmin(Admin admin) {
        if (admin == null || admin.getId() == null) {
            throw new InvalidAdminDataException("Admin and ID cannot be null");
        }

        try {
            Admin existingAdmin = getAdminById(admin.getId());
            adminValidator.validateUpdate(admin);

            if (admin.getPassword() == null || admin.getPassword().equals(existingAdmin.getPassword())) {
                admin.setPassword(existingAdmin.getPassword());
                admin.setSalt(existingAdmin.getSalt());
            } else {
                String salt = passwordService.generateSalt();
                admin.setSalt(salt);
                admin.setPassword(passwordService.hashPassword(admin.getPassword(), salt));
            }

            return adminRepository.save(admin);
        } catch (AdminNotFoundException | InvalidAdminDataException | DuplicateAdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceException("Failed to update admin with id: " + admin.getId(), e);
        }
    }

    @Override
    public void deleteAdmin(Long id) {
        try {
            Admin admin = getAdminById(id);
            adminRepository.deleteById(id);

            if (admin.getProfilePhotoPath() != null && !admin.getProfilePhotoPath().isEmpty()) {
                fileStorageService.deleteFile(admin.getProfilePhotoPath());
            }
        } catch (AdminNotFoundException e) {
            throw e;
        } catch (FileNotFoundException | FileStorageException e) {
            throw new AdminServiceException("Admin deleted but failed to delete profile photo: " + id);
        } catch (Exception e) {
            throw new AdminServiceException("Failed to delete admin: " + id, e);
        }
    }
}
