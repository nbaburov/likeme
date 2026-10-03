
package fontys.sem3.likeme.domain.analytics;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderAnalytics {
    private Long influencerId;
    private Map<String, Long> ordersByCountry;
}