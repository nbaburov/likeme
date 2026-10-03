package fontys.sem3.likeme.business.impl.user.admin;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.exception.user.admin.AdminServiceException;
import fontys.sem3.likeme.business.exception.user.admin.InvalidAdminDataException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.business.validator.user.admin.AdminValidator;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.admin.AdminRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {
        @Mock
        private AdminRepository adminRepository;
        @Mock
        private AdminValidator adminValidator;
        @Mock
        private PasswordService passwordService;
        @Mock
        private FileStorageService fileStorageService;

        @InjectMocks
        private AdminServiceImpl adminService;

        private Admin testAdmin;
        private static final Long ADMIN_ID = 1L;
        private static final String USERNAME = "testadmin";
        private static final String PASSWORD = "password123";
        private static final String SALT = "salt123";
        private static final String HASHED_PASSWORD = "hashedPassword123";
        private static final String PROFILE_PHOTO_PATH = "path/to/photo.jpg";

        @BeforeEach
        void setUp() {
                testAdmin = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .salt(SALT)
                                .build();
        }

        @Test
        void getAllAdmins_Success() {
                Admin secondAdmin = Admin.builder()
                                .id(2L)
                                .username("admin2")
                                .password("pass2")
                                .salt("salt2")
                                .build();
                List<Admin> expectedAdmins = List.of(testAdmin, secondAdmin);
                when(adminRepository.findAll()).thenReturn(expectedAdmins);

                List<Admin> actualAdmins = adminService.getAllAdmins();

                assertEquals(expectedAdmins.size(), actualAdmins.size(), "Should return correct number of admins");
                assertEquals(expectedAdmins, actualAdmins, "Should return exact list of admins");
                verify(adminRepository).findAll();
        }

        @Test
        void getAdminById_Success() {
                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));

                Admin actualAdmin = adminService.getAdminById(ADMIN_ID);

                assertNotNull(actualAdmin);
                assertEquals(ADMIN_ID, actualAdmin.getId());
                assertEquals(USERNAME, actualAdmin.getUsername());
                assertEquals(PASSWORD, actualAdmin.getPassword());
                assertEquals(SALT, actualAdmin.getSalt());
                verify(adminRepository).findById(ADMIN_ID);
        }

        @Test
        void getAdminByUsername_Success() {
                when(adminRepository.findByUsername(USERNAME)).thenReturn(Optional.of(testAdmin));

                Admin actualAdmin = adminService.getAdminByUsername(USERNAME);

                assertNotNull(actualAdmin);
                assertEquals(ADMIN_ID, actualAdmin.getId());
                assertEquals(USERNAME, actualAdmin.getUsername());
                assertEquals(PASSWORD, actualAdmin.getPassword());
                assertEquals(SALT, actualAdmin.getSalt());
                verify(adminRepository).findByUsername(USERNAME);
        }

        @Test
        void createAdmin_Success() {
                Admin adminToCreate = Admin.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .build();

                Admin expectedAdminToSave = Admin.builder()
                                .username(USERNAME)
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .build();

                Admin expectedSavedAdmin = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(HASHED_PASSWORD)
                                .salt(SALT)
                                .build();

                when(passwordService.generateSalt()).thenReturn(SALT);
                when(passwordService.hashPassword(PASSWORD, SALT)).thenReturn(HASHED_PASSWORD);
                when(adminRepository.save(expectedAdminToSave)).thenReturn(expectedSavedAdmin);

                Admin actualAdmin = adminService.createAdmin(adminToCreate);

                assertEquals(expectedSavedAdmin, actualAdmin);
                verify(adminValidator).validateCreate(adminToCreate);
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(PASSWORD, SALT);
                verify(adminRepository).save(expectedAdminToSave);
        }

        @Test
        void updateAdmin_WithNewPassword() {
                String newPassword = "newPassword123";
                String newSalt = "newSalt123";
                String newHashedPassword = "newHashedPassword123";

                Admin adminToUpdate = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(newPassword)
                                .build();

                Admin expectedAdminToSave = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(newHashedPassword)
                                .salt(newSalt)
                                .build();

                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
                when(passwordService.generateSalt()).thenReturn(newSalt);
                when(passwordService.hashPassword(newPassword, newSalt)).thenReturn(newHashedPassword);
                when(adminRepository.save(expectedAdminToSave)).thenReturn(expectedAdminToSave);

                Admin actualAdmin = adminService.updateAdmin(adminToUpdate);

                assertEquals(expectedAdminToSave, actualAdmin);
                verify(adminValidator).validateUpdate(adminToUpdate);
                verify(adminRepository).findById(ADMIN_ID);
                verify(passwordService).generateSalt();
                verify(passwordService).hashPassword(newPassword, newSalt);
                verify(adminRepository).save(expectedAdminToSave);
        }

        @Test
        void updateAdmin_WithoutNewPassword() {
                Admin adminToUpdate = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .build();

                Admin expectedAdminToSave = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .salt(SALT)
                                .build();

                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
                when(adminRepository.save(expectedAdminToSave)).thenReturn(expectedAdminToSave);

                Admin actualAdmin = adminService.updateAdmin(adminToUpdate);

                assertEquals(expectedAdminToSave, actualAdmin);
                verify(adminValidator).validateUpdate(adminToUpdate);
                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).save(expectedAdminToSave);
                verify(passwordService, never()).generateSalt();
                verify(passwordService, never()).hashPassword(anyString(), anyString());
        }

        @Test
        void deleteAdmin_Success() {
                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));

                adminService.deleteAdmin(ADMIN_ID);

                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).deleteById(ADMIN_ID);
        }

        @Test
        void deleteAdmin_NotFound_ThrowsAdminNotFoundException() {
                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.empty());

                assertThrows(AdminNotFoundException.class, () -> adminService.deleteAdmin(ADMIN_ID));
                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository, never()).deleteById(ADMIN_ID);
        }

        @Test
        void getAllAdmins_WhenRepositoryThrowsException_ShouldThrowAdminServiceException() {
                when(adminRepository.findAll()).thenThrow(new RuntimeException("Database error"));

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.getAllAdmins());
                assertEquals("Failed to retrieve all admins", exception.getMessage());
                verify(adminRepository).findAll();
        }

        @Test
        void getAdminById_WhenIdIsNull_ShouldThrowInvalidAdminDataException() {
                InvalidAdminDataException exception = assertThrows(
                                InvalidAdminDataException.class,
                                () -> adminService.getAdminById(null));
                assertEquals("Admin ID cannot be null", exception.getMessage());
                verify(adminRepository, never()).findById(any());
        }

        @Test
        void createAdmin_WhenAdminIsNull_ShouldThrowInvalidAdminDataException() {
                InvalidAdminDataException exception = assertThrows(
                                InvalidAdminDataException.class,
                                () -> adminService.createAdmin(null));
                assertEquals("Admin cannot be null", exception.getMessage());
                verify(adminValidator, never()).validateCreate(any());
        }

        @Test
        void deleteAdmin_WithProfilePhoto() {
                Admin adminWithPhoto = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .salt(SALT)
                                .profilePhotoPath(PROFILE_PHOTO_PATH)
                                .build();

                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminWithPhoto));

                adminService.deleteAdmin(ADMIN_ID);

                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).deleteById(ADMIN_ID);
                verify(fileStorageService).deleteFile(PROFILE_PHOTO_PATH);
        }

        @Test
        void deleteAdmin_WithoutProfilePhoto() {
                Admin adminWithoutPhoto = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .salt(SALT)
                                .profilePhotoPath(null)
                                .build();

                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminWithoutPhoto));

                adminService.deleteAdmin(ADMIN_ID);

                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).deleteById(ADMIN_ID);
                verify(fileStorageService, never()).deleteFile(any());
        }

        @Test
        void deleteAdmin_WhenFileStorageFailsButAdminDeleted() {
                Admin adminWithPhoto = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .salt(SALT)
                                .profilePhotoPath(PROFILE_PHOTO_PATH)
                                .build();

                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(adminWithPhoto));
                doThrow(new FileStorageException("File delete failed"))
                                .when(fileStorageService).deleteFile(PROFILE_PHOTO_PATH);

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.deleteAdmin(ADMIN_ID));
                assertEquals("Admin deleted but failed to delete profile photo: " + ADMIN_ID,
                                exception.getMessage());

                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).deleteById(ADMIN_ID);
                verify(fileStorageService).deleteFile(PROFILE_PHOTO_PATH);
        }

        @Test
        void getAdminById_WhenRepositoryThrowsException_ShouldThrowAdminServiceException() {
                when(adminRepository.findById(ADMIN_ID)).thenThrow(new RuntimeException("Database error"));

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.getAdminById(ADMIN_ID));
                assertEquals("Error retrieving admin with id: " + ADMIN_ID, exception.getMessage());
                verify(adminRepository).findById(ADMIN_ID);
        }

        @Test
        void getAdminByUsername_WhenUsernameIsNull_ShouldThrowInvalidAdminDataException() {
                InvalidAdminDataException exception = assertThrows(
                                InvalidAdminDataException.class,
                                () -> adminService.getAdminByUsername(null));
                assertEquals("Username cannot be null", exception.getMessage());
                verify(adminRepository, never()).findByUsername(USERNAME);
        }

        @Test
        void getAdminByUsername_WhenRepositoryThrowsException_ShouldThrowAdminServiceException() {
                when(adminRepository.findByUsername(USERNAME)).thenThrow(new RuntimeException("Database error"));

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.getAdminByUsername(USERNAME));
                assertEquals("Error retrieving admin with username: " + USERNAME, exception.getMessage());
                verify(adminRepository).findByUsername(USERNAME);
        }

        @Test
        void updateAdmin_WhenAdminOrIdIsNull_ShouldThrowInvalidAdminDataException() {
                Admin adminWithNullId = Admin.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .build();

                InvalidAdminDataException exceptionForNull = assertThrows(
                                InvalidAdminDataException.class,
                                () -> adminService.updateAdmin(null));
                assertEquals("Admin and ID cannot be null", exceptionForNull.getMessage());

                InvalidAdminDataException exceptionForNullId = assertThrows(
                                InvalidAdminDataException.class,
                                () -> adminService.updateAdmin(adminWithNullId));
                assertEquals("Admin and ID cannot be null", exceptionForNullId.getMessage());

                verify(adminRepository, never()).findById(ADMIN_ID);
                verify(adminRepository, never()).save(testAdmin);
        }

        @Test
        void updateAdmin_WhenRepositoryThrowsException_ShouldThrowAdminServiceException() {
                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
                when(adminRepository.save(testAdmin)).thenThrow(new RuntimeException("Database error"));

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.updateAdmin(testAdmin));
                assertEquals("Failed to update admin with id: " + ADMIN_ID, exception.getMessage());
                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).save(testAdmin);
        }

        @Test
        void deleteAdmin_WhenRepositoryThrowsException_ShouldThrowAdminServiceException() {
                when(adminRepository.findById(ADMIN_ID)).thenReturn(Optional.of(testAdmin));
                doThrow(new RuntimeException("Database error"))
                                .when(adminRepository).deleteById(ADMIN_ID);

                AdminServiceException exception = assertThrows(
                                AdminServiceException.class,
                                () -> adminService.deleteAdmin(ADMIN_ID));
                assertEquals("Failed to delete admin: " + ADMIN_ID, exception.getMessage());
                verify(adminRepository).findById(ADMIN_ID);
                verify(adminRepository).deleteById(ADMIN_ID);
        }
}
