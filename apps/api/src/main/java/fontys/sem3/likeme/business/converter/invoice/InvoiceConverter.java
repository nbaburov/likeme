package fontys.sem3.likeme.business.converter.invoice;

import java.util.Date;

import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceConversionException;
import fontys.sem3.likeme.controller.dto.invoice.CreateInvoiceRequest;
import fontys.sem3.likeme.controller.dto.invoice.InvoiceResponse;
import fontys.sem3.likeme.controller.dto.invoice.UpdateInvoiceRequest;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceConverter {
    public static Invoice requestToDomain(CreateInvoiceRequest request) {
        if (request == null) {
            throw new InvalidRequest("Invoice request cannot be null");
        }

        try {
            Date now = new Date();
            return Invoice.builder()
                    .amount(request.getAmount())
                    .status(request.getStatus() != null ? request.getStatus() : InvoiceStatus.PENDING)
                    .createdOn(now)
                    .updatedOn(now)
                    .build();
        } catch (Exception e) {
            throw new InvalidInvoiceConversionException(
                    "Failed to convert create request to invoice domain: " + e.getMessage(), e);
        }
    }

    public static Invoice requestToDomain(UpdateInvoiceRequest request, Long id, Invoice existingInvoice) {
        if (request == null || existingInvoice == null) {
            throw new InvalidRequest("Update request and existing invoice cannot be null");
        }

        try {
            return Invoice.builder()
                    .id(id)
                    .amount(request.getAmount() != null ? request.getAmount() : existingInvoice.getAmount())
                    .status(request.getStatus() != null ? request.getStatus() : existingInvoice.getStatus())
                    .createdOn(existingInvoice.getCreatedOn())
                    .updatedOn(new Date())
                    .build();
        } catch (Exception e) {
            throw new InvalidInvoiceConversionException(
                    "Failed to convert update request to invoice domain: " + e.getMessage(), e);
        }
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        if (invoice == null) {
            throw new InvalidRequest("Invoice cannot be null");
        }

        try {
            return InvoiceResponse.builder()
                    .id(invoice.getId())
                    .amount(invoice.getAmount())
                    .status(invoice.getStatus())
                    .createdOn(invoice.getCreatedOn())
                    .updatedOn(invoice.getUpdatedOn())
                    .build();
        } catch (Exception e) {
            throw new InvalidInvoiceConversionException(
                    "Failed to convert invoice to response: " + e.getMessage(), e);
        }
    }
} 