package fontys.sem3.likeme.business.exception.order;

import lombok.Getter;

@Getter
public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException(String message) {
        super(message);
    }
}