package fontys.sem3.likeme.business.impl.analytics;

import java.util.Map;

import org.springframework.stereotype.Service;

import fontys.sem3.likeme.business.exception.analytics.AnalyticsException;
import fontys.sem3.likeme.business.exception.analytics.InvalidAnalyticsRequestException;
import fontys.sem3.likeme.business.interfaces.analytics.AnalyticsService;
import fontys.sem3.likeme.domain.analytics.OrderAnalytics;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final OrderRepository orderRepository;

    @Override
    public OrderAnalytics getOrderAnalyticsByInfluencer(Long influencerId) {
        if (influencerId == null) {
            throw new InvalidAnalyticsRequestException("Influencer ID cannot be null");
        }

        try {
            Map<String, Long> ordersByCountry = orderRepository
                    .findOrdersByInfluencerGroupedByClientCountry(influencerId);

            if (ordersByCountry.isEmpty()) {
                throw new InvalidAnalyticsRequestException("No orders found for influencer with ID: " + influencerId);
            }

            return OrderAnalytics.builder()
                    .influencerId(influencerId)
                    .ordersByCountry(ordersByCountry)
                    .build();
        } catch (InvalidAnalyticsRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new AnalyticsException("Failed to generate analytics for influencer: " + e.getMessage(), e);
        }
    }
}