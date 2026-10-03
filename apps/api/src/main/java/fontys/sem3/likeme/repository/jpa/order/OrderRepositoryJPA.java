package fontys.sem3.likeme.repository.jpa.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.repository.impl.entity.order.OrderEntity;

@Repository
public interface OrderRepositoryJPA extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByOfferId(Long offerId);

    Optional<OrderEntity> findByInvoiceId(Long invoiceId);

    List<OrderEntity> findByOrderedById(Long clientId);

    List<OrderEntity> findByOfferCreatedById(Long influencerId);

    @Query("SELECT c.billingDetails.country as country, COUNT(o) as orderCount " + // Selects the country from billing details and counts the number of orders
            "FROM OrderEntity o " + // Specifies the main entity to query from, which is OrderEntity
            "JOIN o.orderedBy c " + // Joins the OrderEntity with the client (orderedBy) to access client details
            "WHERE o.offer.createdBy.id = :influencerId " + // Filters the results to only include orders created by the specified influencer
            "GROUP BY c.billingDetails.country " + // Groups the results by the country of the billing details
            "ORDER BY COUNT(o) DESC") // Orders the results by the count of orders in descending order
    List<Object[]> findOrdersByInfluencerGroupedByClientCountry(@Param("influencerId") Long influencerId);
}
