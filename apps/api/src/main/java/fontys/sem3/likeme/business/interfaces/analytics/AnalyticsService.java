package fontys.sem3.likeme.business.interfaces.analytics;

import fontys.sem3.likeme.domain.analytics.OrderAnalytics;

public interface AnalyticsService {
    OrderAnalytics getOrderAnalyticsByInfluencer(Long influencerId);
}
