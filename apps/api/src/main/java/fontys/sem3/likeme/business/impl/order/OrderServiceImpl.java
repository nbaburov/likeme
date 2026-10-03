package fontys.sem3.likeme.business.impl.order;

import fontys.sem3.likeme.business.exception.order.DuplicateOrderException;
import fontys.sem3.likeme.business.exception.order.InvalidOrderDataException;
import fontys.sem3.likeme.business.exception.order.OrderNotFoundException;
import fontys.sem3.likeme.business.exception.order.OrderServiceException;
import fontys.sem3.likeme.business.interfaces.offer.OfferService;
import fontys.sem3.likeme.business.interfaces.order.OrderService;
import fontys.sem3.likeme.business.interfaces.invoice.InvoiceService;
import fontys.sem3.likeme.business.validator.order.OrderValidator;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.order.OrderStatus;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final OfferService offerService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        if (order == null) {
            throw new InvalidOrderDataException("Order cannot be null");
        }

        try {
            Invoice invoice = invoiceService.createInvoice(Invoice.builder()
                    .amount(BigDecimal.valueOf(order.getOffer().getPrice()))
                    .build());

            order.setInvoice(invoice);
            orderValidator.validateCreate(order);
            return orderRepository.save(order);
        } catch (InvalidOrderDataException | DuplicateOrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to create order", e);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        try {
            getOrder(id); // Check if exists
            orderRepository.deleteById(id);
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Failed to delete order: " + id, e);
        }
    }

    @Override
    public Order getOrder(Long id) {
        if (id == null) {
            throw new InvalidOrderDataException("Order ID cannot be null");
        }
        try {
            return orderRepository.findById(id)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Error retrieving order with id: " + id, e);
        }
    }

    @Override
    public List<Order> getOrdersByOffer(Long offerId) {
        try {
            // Verify offer exists
            offerService.getOffer(offerId);
            return orderRepository.findByOfferId(offerId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve orders for offer: " + offerId, e);
        }
    }

    @Override
    public Order getOrderByInvoice(Long invoiceId) {
        if (invoiceId == null) {
            throw new InvalidOrderDataException("Invoice ID cannot be null");
        }
        try {
            return orderRepository.findByInvoiceId(invoiceId)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found for invoice id: " + invoiceId));
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderServiceException("Error retrieving order for invoice: " + invoiceId, e);
        }
    }

    @Override
    public List<Order> getOrdersByClient(Long clientId) {
        try {
            return orderRepository.findByOrderedClientId(clientId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve orders for client: " + clientId, e);
        }
    }

    @Override
    public List<Order> getOrdersByInfluencer(Long influencerId) {
        try {
            return orderRepository.findByInfluencerId(influencerId);
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve orders for influencer: " + influencerId, e);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            throw new OrderServiceException("Failed to retrieve all orders", e);
        }
    }

    @Override
    @Transactional
    public Order completeOrder(Long id, Influencer influencer) {
        Order order = getOrder(id);

        // Validate completion
        orderValidator.validateCompletion(order, influencer);

        // Update status
        order.setStatus(OrderStatus.COMPLETE);
        order.setUpdatedBy(influencer);
        order.setUpdatedOn(new Date());

        return orderRepository.save(order);
    }
}