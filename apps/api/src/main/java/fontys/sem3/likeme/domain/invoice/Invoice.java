package fontys.sem3.likeme.domain.invoice;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Invoice {
    @Setter(AccessLevel.NONE)
    private Long id;
    private BigDecimal amount;
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.PENDING;
    @Builder.Default
    private Date createdOn = new Date();
    @Builder.Default
    private Date updatedOn = new Date();
}
