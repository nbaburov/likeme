package fontys.sem3.likeme.business.exception.platform;

public class PlatformSettingsConversionException extends RuntimeException {
    public PlatformSettingsConversionException(String message) {
        super(message);
    }

    public PlatformSettingsConversionException(String message, Throwable cause) {
        super(message, cause);
    }
} 