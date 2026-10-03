package fontys.sem3.likeme.controller.dto.invoice;

import java.math.BigDecimal;
import java.util.Date;

import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private BigDecimal amount;
    private InvoiceStatus status;
    private Date createdOn;
    private Date updatedOn;
}