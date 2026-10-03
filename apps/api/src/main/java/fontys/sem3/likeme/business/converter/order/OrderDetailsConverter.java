package fontys.sem3.likeme.business.converter.order;

import fontys.sem3.likeme.business.exception.order.InvalidOrderConversionException;
import fontys.sem3.likeme.controller.dto.order.OrderDetailsRequest;
import fontys.sem3.likeme.controller.dto.order.OrderDetailsResponse;
import fontys.sem3.likeme.domain.order.OrderDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailsConverter {
    public static OrderDetails toDomain(OrderDetailsRequest request) {
        if (request == null) {
            return OrderDetails.builder()
                    .postId(null)
                    .comment(null)
                    .build();
        }

        try {
            return OrderDetails.builder()
                    .postId(request.getPostId() != null ? request.getPostId() : null)
                    .comment(request.getComment() != null ? request.getComment() : null)
                    .build();
        } catch (Exception e) {
            throw new InvalidOrderConversionException(
                    "Failed to convert request to order details domain: " + e.getMessage(), e);
        }
    }

    public static OrderDetailsResponse toResponse(OrderDetails details) {
        if (details == null) {
            return OrderDetailsResponse.builder()
                    .postId(null)
                    .comment(null)
                    .build();
        }

        try {
            return OrderDetailsResponse.builder()
                    .postId(details.getPostId() != null ? details.getPostId() : null)
                    .comment(details.getComment() != null ? details.getComment() : null)
                    .build();
        } catch (Exception e) {
            throw new InvalidOrderConversionException(
                    "Failed to convert order details to response: " + e.getMessage(), e);
        }
    }
}