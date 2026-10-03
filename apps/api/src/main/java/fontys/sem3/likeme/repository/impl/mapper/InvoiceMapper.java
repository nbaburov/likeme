package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import fontys.sem3.likeme.repository.impl.entity.invoice.InvoiceEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceMapper {
    public static InvoiceEntity mapToEntity(Invoice invoice) {
        if (invoice == null)
            return null;

        return InvoiceEntity.builder()
                .id(invoice.getId())
                .amount(invoice.getAmount())
                .status(invoice.getStatus() != null ? invoice.getStatus() : InvoiceStatus.PENDING)
                .createdOn(invoice.getCreatedOn())
                .updatedOn(invoice.getUpdatedOn())
                .build();
    }

    public static Invoice mapToDomain(InvoiceEntity entity) {
        if (entity == null)
            return null;

        return Invoice.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .status(entity.getStatus() != null ? entity.getStatus() : InvoiceStatus.PENDING)
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .build();
    }
}