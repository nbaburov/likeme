package fontys.sem3.likeme.business.exception.offer;

import lombok.Getter;

@Getter
public class InvalidOfferDataException extends RuntimeException {
    public InvalidOfferDataException(String message) {
        super(message);
    }
}