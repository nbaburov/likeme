package fontys.sem3.likeme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.order.OrderConverter;
import fontys.sem3.likeme.business.interfaces.notification.NotificationService;
import fontys.sem3.likeme.business.interfaces.offer.OfferService;
import fontys.sem3.likeme.business.interfaces.order.OrderService;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.controller.dto.order.CreateOrderRequest;
import fontys.sem3.likeme.controller.dto.order.OrderResponse;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.user.client.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Endpoints for order management")
public class OrderController extends BaseController {
    private final OrderService orderService;
    private final ClientService clientService;
    private final OfferService offerService;
    private final NotificationService notificationService;

    @Operation(summary = "Create order", description = "Create a new order")
    @PostMapping
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        Client client = clientService.getClient(getCurrentUserId());
        Offer offer = offerService.getOffer(request.getOfferId());

        Order order = OrderConverter.requestToDomain(request, client, offer);
        return new ResponseEntity<>(OrderConverter.toResponse(orderService.createOrder(order)), HttpStatus.CREATED);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve order details by ID")
    @GetMapping("/{id}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderConverter.toResponse(orderService.getOrder(id)));
    }

    @Operation(summary = "Get orders by offer", description = "Retrieve orders by offer ID")
    @GetMapping("/offer/{offerId}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<List<OrderResponse>> getOrdersByOffer(@PathVariable Long offerId) {
        List<OrderResponse> responses = orderService.getOrdersByOffer(offerId).stream()
                .map(OrderConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get order by invoice", description = "Retrieve order by invoice ID")
    @GetMapping("/invoice/{invoiceId}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<OrderResponse> getOrderByInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(OrderConverter.toResponse(orderService.getOrderByInvoice(invoiceId)));
    }

    @Operation(summary = "Get orders by client", description = "Retrieve orders by client ID")
    @GetMapping("/client/{clientId}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<List<OrderResponse>> getOrdersByClient(@PathVariable Long clientId) {
        List<OrderResponse> responses = orderService.getOrdersByClient(clientId).stream()
                .map(OrderConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get orders by influencer", description = "Retrieve orders by influencer ID")
    @GetMapping("/influencer/{influencerId}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<List<OrderResponse>> getOrdersByInfluencer(@PathVariable Long influencerId) {
        List<OrderResponse> responses = orderService.getOrdersByInfluencer(influencerId).stream()
                .map(OrderConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all orders", description = "Retrieve all orders")
    @GetMapping
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders().stream()
                .map(OrderConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Complete order", description = "Complete an order")
    @PutMapping("/{id}/complete")
    @RolesAllowed({ "INFLUENCER" })
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        validateOwnership(order.getOffer().getCreatedBy().getId());

        Order completedOrder = orderService.completeOrder(id, order.getOffer().getCreatedBy());

        // Send notification to client
        notificationService.sendOrderCompletedNotification(
                order.getOrderedBy().getId(),
                id);

        return ResponseEntity.ok(OrderConverter.toResponse(completedOrder));
    }

    @Operation(summary = "Delete order", description = "Delete an order")
    @DeleteMapping("/{id}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}