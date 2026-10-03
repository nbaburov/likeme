package fontys.sem3.likeme.business.validator.order;

import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.exception.order.DuplicateOrderException;
import fontys.sem3.likeme.business.exception.order.InvalidOrderDataException;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.order.OrderStatus;
import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderRepository orderRepository;

    public void validateCreate(Order order) {
        if (order.getDetails() == null) {
            throw new InvalidOrderDataException("Order details cannot be null");
        }

        if (order.getOrderedBy() == null) {
            throw new InvalidOrderDataException("Order must have a client");
        }

        // Check for duplicate order details
        orderRepository.findAll().stream()
                .filter(existingOrder -> existingOrder.getDetails() != null)
                .filter(existingOrder -> existingOrder.getDetails().equals(order.getDetails()))
                .findFirst()
                .ifPresent(existingOrder -> {
                    throw new DuplicateOrderException("Duplicate order details found");
                });
    }

    public void validateCompletion(Order order, Influencer influencer) {
        if (order.getStatus() == OrderStatus.COMPLETE) {
            throw new InvalidOrderDataException("Order is already completed");
        }

        if (!order.getOffer().getCreatedBy().getId().equals(influencer.getId())) {
            throw new InvalidOrderDataException("Only the offer creator can complete this order");
        }

        if (order.getInvoice() == null || order.getInvoice().getStatus() != InvoiceStatus.PAID) {
            throw new InvalidOrderDataException("Cannot complete order until invoice is paid");
        }
    }
}