package fontys.sem3.likeme.business.exception.order;

import lombok.Getter;

@Getter
public class OrderServiceException extends RuntimeException {
    public OrderServiceException(String message) {
        super(message);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 