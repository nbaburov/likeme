package fontys.sem3.likeme.repository.impl.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.business.exception.analytics.AnalyticsException;
import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.repository.impl.mapper.OrderMapper;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;
import fontys.sem3.likeme.repository.jpa.order.OrderRepositoryJPA;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderRepositoryJPA orderRepositoryJPA;

    @Override
    public Order save(Order order) {
        return OrderMapper.mapToDomain(
                orderRepositoryJPA.save(
                        OrderMapper.mapToEntity(order)));
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepositoryJPA.findById(id)
                .map(OrderMapper::mapToDomain);
    }

    @Override
    public List<Order> findByOfferId(Long offerId) {
        return orderRepositoryJPA.findByOfferId(offerId)
                .stream()
                .map(OrderMapper::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Order> findByInvoiceId(Long invoiceId) {
        return orderRepositoryJPA.findByInvoiceId(invoiceId)
                .map(OrderMapper::mapToDomain);
    }

    @Override
    public List<Order> findByOrderedClientId(Long clientId) {
        return orderRepositoryJPA.findByOrderedById(clientId)
                .stream()
                .map(OrderMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Order> findByInfluencerId(Long influencerId) {
        return orderRepositoryJPA.findByOfferCreatedById(influencerId)
                .stream()
                .map(OrderMapper::mapToDomain)
                .toList();
    }

    @Override
    public List<Order> findAll() {
        return orderRepositoryJPA.findAll()
                .stream()
                .map(OrderMapper::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        orderRepositoryJPA.deleteById(id);
    }

    @Override
    public Map<String, Long> findOrdersByInfluencerGroupedByClientCountry(Long influencerId) {
        try {
            return orderRepositoryJPA.findOrdersByInfluencerGroupedByClientCountry(influencerId)
                    .stream()
                    .collect(Collectors.toMap(
                            result -> (String) result[0], 
                            result -> ((Number) result[1]).longValue(), 
                            (existing, replacement) -> existing));
        } catch (Exception e) {
            throw new AnalyticsException("Failed to retrieve order analytics by country", e);
        }
    }
}
