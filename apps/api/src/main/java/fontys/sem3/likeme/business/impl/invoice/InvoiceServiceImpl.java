package fontys.sem3.likeme.business.impl.invoice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.invoice.InvalidInvoiceDataException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceNotFoundException;
import fontys.sem3.likeme.business.exception.invoice.InvoiceServiceException;
import fontys.sem3.likeme.business.interfaces.invoice.InvoiceService;
import fontys.sem3.likeme.business.validator.invoice.InvoiceValidator;
import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.repository.interfaces.invoice.InvoiceRepository;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceValidator invoiceValidator;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new InvalidInvoiceDataException("Invoice cannot be null");
        }

        try {
            invoiceValidator.validateCreate(invoice);
            return invoiceRepository.save(invoice);
        } catch (InvalidInvoiceDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InvoiceServiceException("Failed to create invoice", e);
        }
    }

    @Override
    @Transactional
    public Invoice updateInvoice(Invoice invoice) {
        if (invoice == null || invoice.getId() == null) {
            throw new InvalidInvoiceDataException("Invoice and ID cannot be null");
        }

        try {
            getInvoice(invoice.getId());
            return invoiceRepository.save(invoice);
        } catch (InvoiceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvoiceServiceException("Failed to update invoice with id: " + invoice.getId(), e);
        }
    }

    @Override
    public void deleteInvoice(Long id) {
        try {
            getInvoice(id);
            invoiceRepository.deleteById(id);
        } catch (InvoiceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvoiceServiceException("Failed to delete invoice: " + id, e);
        }
    }

    @Override
    public Invoice getInvoice(Long id) {
        if (id == null) {
            throw new InvalidInvoiceDataException("Invoice ID cannot be null");
        }
        try {
            return invoiceRepository.findById(id)
                    .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        } catch (InvoiceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvoiceServiceException("Error retrieving invoice with id: " + id, e);
        }
    }

    @Override
    public Invoice getInvoiceByOrderId(Long orderId) {
        if (orderId == null) {
            throw new InvalidInvoiceDataException("Order ID cannot be null");
        }
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new InvoiceNotFoundException("Order not found for id: " + orderId));
            if (order.getInvoice() == null) {
                throw new InvoiceNotFoundException("Invoice not found for order: " + orderId);
            }
            return order.getInvoice();
        } catch (InvoiceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InvoiceServiceException("Error retrieving invoice for order: " + orderId, e);
        }
    }

    @Override
    public List<Invoice> getAllInvoices() {
        try {
            return invoiceRepository.findAll();
        } catch (Exception e) {
            throw new InvoiceServiceException("Failed to retrieve all invoices", e);
        }
    }

    @Override
    public List<Invoice> getInvoicesByClientId(Long clientId) {
        List<Order> orders = orderRepository.findByOrderedClientId(clientId);
        // Find the invoices for the orders
        return orders.stream()
                .map(Order::getInvoice)
                .toList();
    }

    @Override
    public List<Invoice> getInvoicesByInfluencerId(Long influencerId) {
        List<Order> orders = orderRepository.findByInfluencerId(influencerId);
        return orders.stream()
                .map(Order::getInvoice)
                .toList();
    }
}
