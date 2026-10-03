package fontys.sem3.likeme.business.exception.order;

import lombok.Getter;

@Getter
public class InvalidOrderConversionException extends RuntimeException {
    public InvalidOrderConversionException(String message) {
        super(message);
    }

    public InvalidOrderConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}