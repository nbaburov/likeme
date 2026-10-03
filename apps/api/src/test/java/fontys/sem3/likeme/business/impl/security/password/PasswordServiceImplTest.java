package fontys.sem3.likeme.business.impl.security.password;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.security.password.PasswordServiceException;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {
    @InjectMocks
    private PasswordServiceImpl passwordService;

    private static final String TEST_PASSWORD = "TestPassword123!";
    private String salt;

    @BeforeEach
    void setUp() {
        salt = passwordService.generateSalt();
    }

    @Test
    void generateSalt_Success() {
        String generatedSalt = passwordService.generateSalt();

        assertNotNull(generatedSalt);
        assertTrue(generatedSalt.startsWith("$2a$"));
        assertEquals(29, generatedSalt.length());
    }

    @Test
    void hashPassword_ValidInputs_Success() {
        String hashedPassword = passwordService.hashPassword(TEST_PASSWORD, salt);

        assertNotNull(hashedPassword);
        assertNotEquals(TEST_PASSWORD, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
    }

    @Test
    void hashPassword_NullPassword_ThrowsException() {
        PasswordServiceException exception = assertThrows(
                PasswordServiceException.class,
                () -> passwordService.hashPassword(null, salt));

        assertEquals("Password and salt cannot be null", exception.getMessage());
    }

    @Test
    void hashPassword_NullSalt_ThrowsException() {
        PasswordServiceException exception = assertThrows(
                PasswordServiceException.class,
                () -> passwordService.hashPassword(TEST_PASSWORD, null));

        assertEquals("Password and salt cannot be null", exception.getMessage());
    }

    @Test
    void verifyPassword_CorrectPassword_ReturnsTrue() {
        String hashedPassword = passwordService.hashPassword(TEST_PASSWORD, salt);

        assertTrue(passwordService.verifyPassword(TEST_PASSWORD, hashedPassword));
    }

    @Test
    void verifyPassword_IncorrectPassword_ReturnsFalse() {
        String hashedPassword = passwordService.hashPassword(TEST_PASSWORD, salt);

        assertFalse(passwordService.verifyPassword("WrongPassword123!", hashedPassword));
    }

    @Test
    void verifyPassword_NullPassword_ThrowsException() {
        String hashedPassword = passwordService.hashPassword(TEST_PASSWORD, salt);

        PasswordServiceException exception = assertThrows(
                PasswordServiceException.class,
                () -> passwordService.verifyPassword(null, hashedPassword));

        assertEquals("Password and hashed password cannot be null", exception.getMessage());
    }

    @Test
    void verifyPassword_NullHashedPassword_ThrowsException() {
        PasswordServiceException exception = assertThrows(
                PasswordServiceException.class,
                () -> passwordService.verifyPassword(TEST_PASSWORD, null));

        assertEquals("Password and hashed password cannot be null", exception.getMessage());
    }

    @Test
    void verifyPassword_InvalidHashedPassword_ThrowsException() {
        PasswordServiceException exception = assertThrows(
                PasswordServiceException.class,
                () -> passwordService.verifyPassword(TEST_PASSWORD, "invalid_hash"));

        assertEquals("Failed to verify password", exception.getMessage());
    }
}