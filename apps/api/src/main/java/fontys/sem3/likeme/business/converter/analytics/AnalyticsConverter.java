package fontys.sem3.likeme.business.converter.analytics;

import fontys.sem3.likeme.business.exception.analytics.InvalidAnalyticsRequestException;
import fontys.sem3.likeme.controller.dto.analytics.OrderAnalyticsResponse;
import fontys.sem3.likeme.domain.analytics.OrderAnalytics;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsConverter {
    public static OrderAnalyticsResponse toResponse(OrderAnalytics analytics) {
        if (analytics == null) {
            throw new InvalidAnalyticsRequestException("Analytics data cannot be null");
        }

        try {
            return OrderAnalyticsResponse.builder()
                    .influencerId(analytics.getInfluencerId())
                    .ordersByCountry(analytics.getOrdersByCountry())
                    .build();
        } catch (Exception e) {
            throw new InvalidAnalyticsRequestException("Failed to convert analytics to response: " + e.getMessage());
        }
    }
} 