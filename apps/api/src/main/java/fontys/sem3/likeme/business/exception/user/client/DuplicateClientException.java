package fontys.sem3.likeme.business.exception.user.client;

public class DuplicateClientException extends RuntimeException {
    public DuplicateClientException(String message) {
        super(message);
    }
}