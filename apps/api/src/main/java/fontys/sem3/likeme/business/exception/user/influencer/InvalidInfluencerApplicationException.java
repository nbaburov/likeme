package fontys.sem3.likeme.business.exception.user.influencer;

public class InvalidInfluencerApplicationException extends RuntimeException {
    public InvalidInfluencerApplicationException(String message) {
        super(message);
    }

    public InvalidInfluencerApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 