package fontys.sem3.likeme.business.exception.user.admin;

public class InvalidAdminConversionException extends RuntimeException {
    public InvalidAdminConversionException(String message) {
        super(message);
    }

    public InvalidAdminConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}
