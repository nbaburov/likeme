package fontys.sem3.likeme.business.exception.user.influencer;

public class InvalidInfluencerApplicationConversionException extends RuntimeException {
    public InvalidInfluencerApplicationConversionException(String message) {
        super(message);
    }

    public InvalidInfluencerApplicationConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
