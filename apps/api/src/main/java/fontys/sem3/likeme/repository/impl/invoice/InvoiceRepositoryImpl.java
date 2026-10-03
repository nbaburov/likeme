package fontys.sem3.likeme.repository.impl.invoice;

import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.repository.impl.mapper.InvoiceMapper;
import fontys.sem3.likeme.repository.interfaces.invoice.InvoiceRepository;
import fontys.sem3.likeme.repository.jpa.invoice.InvoiceRepositoryJPA;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryImpl implements InvoiceRepository {
    private final InvoiceRepositoryJPA invoiceRepositoryJPA;

    @Override
    public Invoice save(Invoice invoice) {
        return InvoiceMapper.mapToDomain(
                invoiceRepositoryJPA.save(
                        InvoiceMapper.mapToEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepositoryJPA.findById(id)
                .map(InvoiceMapper::mapToDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepositoryJPA.findAll()
                .stream()
                .map(InvoiceMapper::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        invoiceRepositoryJPA.deleteById(id);
    }
}
