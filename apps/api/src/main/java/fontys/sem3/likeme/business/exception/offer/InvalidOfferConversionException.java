package fontys.sem3.likeme.business.exception.offer;

import lombok.Getter;

@Getter
public class InvalidOfferConversionException extends RuntimeException {
    public InvalidOfferConversionException(String message) {
        super(message);
    }

    public InvalidOfferConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}