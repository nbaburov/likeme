package fontys.sem3.likeme.controller.dto.order;

import fontys.sem3.likeme.domain.order.OrderStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    @Valid
    private OrderDetailsRequest details;
    private OrderStatus status;
}
