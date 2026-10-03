package fontys.sem3.likeme.business.exception.order;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}