package fontys.sem3.likeme.repository.jpa.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import fontys.sem3.likeme.repository.impl.entity.invoice.InvoiceEntity;

public interface InvoiceRepositoryJPA extends JpaRepository<InvoiceEntity, Long> {
}
