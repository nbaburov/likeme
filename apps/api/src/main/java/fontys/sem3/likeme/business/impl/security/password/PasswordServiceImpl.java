package fontys.sem3.likeme.business.impl.security.password;

import fontys.sem3.likeme.business.exception.security.password.PasswordServiceException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Override
    public String generateSalt() {
        try {
            return BCrypt.gensalt(12);
        } catch (Exception e) {
            throw new PasswordServiceException("Failed to generate salt", e);
        }
    }

    @Override
    public String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new PasswordServiceException("Password and salt cannot be null");
        }
        try {
            return BCrypt.hashpw(password, salt);
        } catch (Exception e) {
            throw new PasswordServiceException("Failed to hash password", e);
        }
    }

    @Override
    public boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            throw new PasswordServiceException("Password and hashed password cannot be null");
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            throw new PasswordServiceException("Failed to verify password", e);
        }
    }
}