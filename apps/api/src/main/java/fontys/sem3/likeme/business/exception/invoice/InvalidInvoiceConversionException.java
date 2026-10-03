package fontys.sem3.likeme.business.exception.invoice;

import lombok.Getter;

@Getter
public class InvalidInvoiceConversionException extends RuntimeException {
    public InvalidInvoiceConversionException(String message) {
        super(message);
    }

    public InvalidInvoiceConversionException(String message, Throwable cause) {
        super(message, cause);
    }
} 