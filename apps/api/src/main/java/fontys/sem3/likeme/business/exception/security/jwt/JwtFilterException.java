package fontys.sem3.likeme.business.exception.security.jwt;

public class JwtFilterException extends RuntimeException {
    public JwtFilterException(String message) {
        super(message);
    }

    public JwtFilterException(String message, Throwable cause) {
        super(message, cause);
    }
} 