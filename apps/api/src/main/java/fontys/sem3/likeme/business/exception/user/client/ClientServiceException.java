package fontys.sem3.likeme.business.exception.user.client;

public class ClientServiceException extends RuntimeException {
    public ClientServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientServiceException(String message) {
        super(message);
    }
} 