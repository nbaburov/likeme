package fontys.sem3.likeme.business.converter.order;

import java.util.Date;

import fontys.sem3.likeme.business.converter.invoice.InvoiceConverter;
import fontys.sem3.likeme.business.converter.offer.OfferConverter;
import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.order.InvalidOrderConversionException;
import fontys.sem3.likeme.controller.dto.order.CreateOrderRequest;
import fontys.sem3.likeme.controller.dto.order.OrderResponse;
import fontys.sem3.likeme.controller.dto.order.UpdateOrderRequest;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.order.OrderStatus;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderConverter {
    public static Order requestToDomain(CreateOrderRequest request, Client client, Offer offer) {
        if (request == null) {
            throw new InvalidRequest("Order request cannot be null");
        }

        try {
            return Order.builder()
                    .offer(offer)
                    .details(OrderDetailsConverter.toDomain(request.getDetails()))
                    .orderedBy(client)
                    .updatedBy(offer.getCreatedBy())
                    .createdOn(new Date())
                    .updatedOn(new Date())
                    .status(OrderStatus.PENDING)
                    .build();
        } catch (Exception e) {
            throw new InvalidOrderConversionException(
                    "Failed to convert create request to order domain: " + e.getMessage(), e);
        }
    }

    public static Order requestToDomain(UpdateOrderRequest request, Long id, Order existingOrder, Influencer updater) {
        if (request == null || existingOrder == null) {
            throw new InvalidRequest("Update request and existing order cannot be null");
        }

        try {
            return Order.builder()
                    .id(id)
                    .offer(existingOrder.getOffer())
                    .details(request.getDetails() != null ? OrderDetailsConverter.toDomain(request.getDetails())
                            : existingOrder.getDetails())
                    .invoice(existingOrder.getInvoice())
                    .orderedBy(existingOrder.getOrderedBy())
                    .updatedBy(updater != null ? (Influencer) updater : existingOrder.getUpdatedBy())
                    .createdOn(existingOrder.getCreatedOn())
                    .updatedOn(new Date())
                    .status(request.getStatus() != null ? request.getStatus() : existingOrder.getStatus())
                    .build();
        } catch (Exception e) {
            throw new InvalidOrderConversionException(
                    "Failed to convert update request to order domain: " + e.getMessage(), e);
        }
    }

    public static OrderResponse toResponse(Order order) {
        if (order == null) {
            throw new InvalidRequest("Order cannot be null");
        }

        try {
            return OrderResponse.builder()
                    .id(order.getId())
                    .offer(OfferConverter.toResponse(order.getOffer()))
                    .details(OrderDetailsConverter.toResponse(order.getDetails()))
                    .invoice(order.getInvoice() != null ? InvoiceConverter.toResponse(order.getInvoice()) : null)
                    .orderedById(order.getOrderedBy() != null ? order.getOrderedBy().getId() : null)
                    .updatedById(order.getUpdatedBy() != null ? order.getUpdatedBy().getId() : null)
                    .createdOn(order.getCreatedOn())
                    .updatedOn(order.getUpdatedOn())
                    .status(order.getStatus())
                    .build();
        } catch (Exception e) {
            throw new InvalidOrderConversionException(
                    "Failed to convert order to response: " + e.getMessage(), e);
        }
    }
}