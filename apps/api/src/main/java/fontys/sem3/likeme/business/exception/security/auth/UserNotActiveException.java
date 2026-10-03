package fontys.sem3.likeme.business.exception.security.auth;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException() {
        super("User account is not active");
    }
}