package fontys.sem3.likeme.controller.dto.analytics;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnalyticsResponse {
    private Long influencerId;
    private Map<String, Long> ordersByCountry;
}
