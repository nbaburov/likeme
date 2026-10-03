package fontys.sem3.likeme.repository.interfaces.invoice;

import java.util.List;
import java.util.Optional;

import fontys.sem3.likeme.domain.invoice.Invoice;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    List<Invoice> findAll();

    void deleteById(Long id);
}
