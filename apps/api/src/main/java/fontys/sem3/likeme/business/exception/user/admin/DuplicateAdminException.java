package fontys.sem3.likeme.business.exception.user.admin;

public class DuplicateAdminException extends RuntimeException {
    public DuplicateAdminException(String message) {
        super(message);
    }
}