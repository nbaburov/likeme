package fontys.sem3.likeme.business.exception.platform;

public class PlatformSettingsNotFoundException extends RuntimeException {
    public PlatformSettingsNotFoundException(String message) {
        super(message);
    }
}