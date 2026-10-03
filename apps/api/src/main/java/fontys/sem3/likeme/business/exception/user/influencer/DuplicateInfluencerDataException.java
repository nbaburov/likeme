package fontys.sem3.likeme.business.exception.user.influencer;

import lombok.Getter;

@Getter
public class DuplicateInfluencerDataException extends RuntimeException {
    private final String field;

    public DuplicateInfluencerDataException(String field, String message) {
        super(message);
        this.field = field;
    }
}
