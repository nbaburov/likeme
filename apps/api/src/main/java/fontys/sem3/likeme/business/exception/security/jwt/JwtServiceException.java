package fontys.sem3.likeme.business.exception.security.jwt;

public class JwtServiceException extends RuntimeException {
    public JwtServiceException(String message) {
        super(message);
    }

    public JwtServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
