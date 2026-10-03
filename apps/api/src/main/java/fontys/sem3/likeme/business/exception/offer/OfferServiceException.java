package fontys.sem3.likeme.business.exception.offer;

import lombok.Getter;

@Getter
public class OfferServiceException extends RuntimeException {
    public OfferServiceException(String message) {
        super(message);
    }

    public OfferServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 