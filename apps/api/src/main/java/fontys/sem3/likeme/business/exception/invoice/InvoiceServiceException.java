package fontys.sem3.likeme.business.exception.invoice;

import lombok.Getter;

@Getter
public class InvoiceServiceException extends RuntimeException {
    public InvoiceServiceException(String message) {
        super(message);
    }

    public InvoiceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}