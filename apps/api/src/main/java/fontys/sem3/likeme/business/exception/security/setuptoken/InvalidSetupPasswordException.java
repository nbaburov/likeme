package fontys.sem3.likeme.business.exception.security.setuptoken;

public class InvalidSetupPasswordException extends RuntimeException {
    public InvalidSetupPasswordException(String message) {
        super(message);
    }
}