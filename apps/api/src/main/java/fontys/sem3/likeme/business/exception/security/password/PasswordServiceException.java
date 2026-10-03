package fontys.sem3.likeme.business.exception.security.password;

public class PasswordServiceException extends RuntimeException {
    public PasswordServiceException(String message) {
        super(message);
    }

    public PasswordServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
