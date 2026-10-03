package fontys.sem3.likeme.business.exception.user.influencer;

public class InfluencerConversionException extends RuntimeException {
    public InfluencerConversionException(String message) {
        super(message);
    }

    public InfluencerConversionException(String message, Throwable cause) {
        super(message, cause);
    }
} 