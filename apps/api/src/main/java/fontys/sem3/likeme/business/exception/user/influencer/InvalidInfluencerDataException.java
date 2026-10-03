package fontys.sem3.likeme.business.exception.user.influencer;

public class InvalidInfluencerDataException extends RuntimeException {
    public InvalidInfluencerDataException(String message) {
        super(message);
    }
    public InvalidInfluencerDataException() {
        super("Influencer ID cannot be null");
    }
}