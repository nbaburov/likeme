package fontys.sem3.likeme.business.validator.invoice;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceDataException;
import fontys.sem3.likeme.business.converter.invoice.InvoiceConverter;
import fontys.sem3.likeme.controller.dto.invoice.UpdateInvoiceRequest;
import fontys.sem3.likeme.domain.invoice.Invoice;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceValidator {

    public void validateCreate(Invoice invoice) {
        // Basic validation only
        if (invoice.getAmount() == null || invoice.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInvoiceDataException("Invoice amount must be positive");
        }
    }

    public void validateUpdate(UpdateInvoiceRequest request, Invoice existingInvoice) {
        if (request == null || (request.getAmount() == null && request.getStatus() == null)) {
            throw new InvalidInvoiceDataException("Update request cannot be empty");
        }

        // Check for no changes
        Invoice updatedInvoice = InvoiceConverter.requestToDomain(request, existingInvoice.getId(), existingInvoice);
        if (existingInvoice.equals(updatedInvoice)) {
            throw new InvalidInvoiceDataException(
                    "No changes detected for invoice with id: " + existingInvoice.getId());
        }
    }
}