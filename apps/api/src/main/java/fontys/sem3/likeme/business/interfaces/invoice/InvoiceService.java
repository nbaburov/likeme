package fontys.sem3.likeme.business.interfaces.invoice;

import java.util.List;

import fontys.sem3.likeme.domain.invoice.Invoice;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);

    Invoice updateInvoice(Invoice invoice);

    void deleteInvoice(Long id);

    Invoice getInvoice(Long id);

    Invoice getInvoiceByOrderId(Long orderId);

    List<Invoice> getAllInvoices();

    List<Invoice> getInvoicesByClientId(Long clientId);

    List<Invoice> getInvoicesByInfluencerId(Long influencerId);
}
