package fontys.sem3.likeme.controller.dto.order;

import fontys.sem3.likeme.controller.dto.invoice.InvoiceResponse;
import fontys.sem3.likeme.controller.dto.offer.OfferResponse;
import fontys.sem3.likeme.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private OfferResponse offer;
    private OrderDetailsResponse details;
    private InvoiceResponse invoice;
    private Long orderedById;
    private Long updatedById;
    private OrderStatus status;
    private Date createdOn;
    private Date updatedOn;
}