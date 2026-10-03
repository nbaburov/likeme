package fontys.sem3.likeme.business.validator.security;

import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordValidator {
    private final PasswordService passwordService;

    public boolean comparePasswords(String rawPassword, String hashedPassword, String salt) {
        String hashedInput = passwordService.hashPassword(rawPassword, salt);
        return hashedInput.equals(hashedPassword);
    }
}