package fontys.sem3.likeme.business.exception.user.influencer;

public class InfluencerServiceException extends RuntimeException {
    public InfluencerServiceException(String message) {
        super(message);
    }

    public InfluencerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 