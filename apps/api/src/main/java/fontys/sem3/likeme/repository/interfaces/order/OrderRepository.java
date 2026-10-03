package fontys.sem3.likeme.repository.interfaces.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fontys.sem3.likeme.domain.order.Order;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByOfferId(Long offerId);

    Optional<Order> findByInvoiceId(Long invoiceId);

    List<Order> findByOrderedClientId(Long clientId);

    List<Order> findByInfluencerId(Long influencerId);

    List<Order> findAll();

    void deleteById(Long id);

    Map<String, Long> findOrdersByInfluencerGroupedByClientCountry(Long influencerId);
}
