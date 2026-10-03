package fontys.sem3.likeme.business.exception.offer;

import lombok.Getter;

@Getter
public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(String message) {
        super(message);
    }
}