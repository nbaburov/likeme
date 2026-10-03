package fontys.sem3.likeme.business.exception.invoice;

import lombok.Getter;

@Getter
public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}