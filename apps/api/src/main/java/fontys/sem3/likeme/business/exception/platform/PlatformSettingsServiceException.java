package fontys.sem3.likeme.business.exception.platform;

public class PlatformSettingsServiceException extends RuntimeException {
    public PlatformSettingsServiceException(String message) {
        super(message);
    }

    public PlatformSettingsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}