package fontys.sem3.likeme.business.exception.invoice;

import lombok.Getter;

@Getter
public class InvalidInvoiceDataException extends RuntimeException {
    public InvalidInvoiceDataException(String message) {
        super(message);
    }
} 