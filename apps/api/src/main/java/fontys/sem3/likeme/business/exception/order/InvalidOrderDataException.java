package fontys.sem3.likeme.business.exception.order;

import lombok.Getter;

@Getter
public class InvalidOrderDataException extends RuntimeException {
    public InvalidOrderDataException(String message) {
        super(message);
    }
} 