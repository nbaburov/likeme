package fontys.sem3.likeme.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.invoice.InvoiceConverter;
import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceDataException;
import fontys.sem3.likeme.business.interfaces.invoice.InvoiceService;
import fontys.sem3.likeme.business.interfaces.notification.NotificationService;
import fontys.sem3.likeme.business.interfaces.order.OrderService;
import fontys.sem3.likeme.business.validator.invoice.InvoiceValidator;
import fontys.sem3.likeme.controller.dto.invoice.InvoiceResponse;
import fontys.sem3.likeme.controller.dto.invoice.UpdateInvoiceRequest;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.invoice.InvoiceStatus;
import fontys.sem3.likeme.domain.order.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Controller", description = "Endpoints for invoice management")
public class InvoiceController extends BaseController {
    private final InvoiceService invoiceService;
    private final InvoiceValidator invoiceValidator;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @Operation(summary = "Get invoice by ID", description = "Retrieve invoice details by ID")
    @GetMapping("/{id}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(InvoiceConverter.toResponse(invoiceService.getInvoice(id)));
    }

    @Operation(summary = "Get invoice by order ID", description = "Retrieve invoice details by order ID")
    @GetMapping("/order/{orderId}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<InvoiceResponse> getInvoiceByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(InvoiceConverter.toResponse(invoiceService.getInvoiceByOrderId(orderId)));
    }

    @Operation(summary = "Get all invoices", description = "Retrieve all invoices")
    @GetMapping
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> responses = invoiceService.getAllInvoices().stream()
                .map(InvoiceConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get invoice by client ID", description = "Retrieve invoice details by client ID")
    @GetMapping("/client/{clientId}")
    @RolesAllowed({ "ADMIN", "CLIENT", "INFLUENCER" })
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByClientId(clientId).stream()
                .map(InvoiceConverter::toResponse)
                .toList());
    }

    @Operation(summary = "Get invoices by influencer ID", description = "Retrieve invoice details by influencer ID")
    @GetMapping("/influencer/{influencerId}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByInfluencerId(@PathVariable Long influencerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByInfluencerId(influencerId).stream()
                .map(InvoiceConverter::toResponse)
                .toList());
    }

    @Operation(summary = "Update invoice", description = "Update invoice details")
    @PutMapping("/{id}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        validateOwnership(id);
        Invoice existingInvoice = invoiceService.getInvoice(id);
        invoiceValidator.validateUpdate(request, existingInvoice);
        Invoice invoice = InvoiceConverter.requestToDomain(request, id, existingInvoice);
        return ResponseEntity.ok(InvoiceConverter.toResponse(invoiceService.updateInvoice(invoice)));
    }

    @Operation(summary = "Delete invoice", description = "Delete an invoice")
    @DeleteMapping("/{id}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        validateOwnership(id);
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pay invoice", description = "Client pays an invoice")
    @PostMapping("/{id}/pay")
    @RolesAllowed({ "CLIENT" })
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoice(id);
        Order order = orderService.getOrderByInvoice(id);
        
        // Validate client ownership
        validateOwnership(order.getOrderedBy().getId());

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new InvalidInvoiceDataException("Invoice already paid");
        }

        // Update invoice status
        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);

        // Send notification
        notificationService.sendInvoicePaidNotification(
            order.getOffer().getCreatedBy().getId(), 
            id
        );
        
        return ResponseEntity.ok(InvoiceConverter.toResponse(updatedInvoice));
    }
}