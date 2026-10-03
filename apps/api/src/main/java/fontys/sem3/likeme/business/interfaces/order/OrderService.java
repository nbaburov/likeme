package fontys.sem3.likeme.business.interfaces.order;

import java.util.List;

import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.domain.user.influencer.Influencer;

public interface OrderService {
    Order createOrder(Order order);

    Order completeOrder(Long id, Influencer influencer);

    void deleteOrder(Long id);

    Order getOrder(Long id);

    List<Order> getOrdersByOffer(Long offerId);

    Order getOrderByInvoice(Long invoiceId);

    List<Order> getOrdersByClient(Long clientId);

    List<Order> getOrdersByInfluencer(Long influencerId);

    List<Order> getAllOrders();
}