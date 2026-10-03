package fontys.sem3.likeme.business.interfaces.security;

public interface PasswordService {
    String generateSalt();

    String hashPassword(String password, String salt);

    boolean verifyPassword(String password, String hashedPassword);
}
