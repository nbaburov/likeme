package fontys.sem3.likeme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.analytics.AnalyticsConverter;
import fontys.sem3.likeme.business.interfaces.analytics.AnalyticsService;
import fontys.sem3.likeme.controller.dto.analytics.OrderAnalyticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/analytics")
@AllArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @Operation(summary = "Get order analytics by influencer")
    @GetMapping("/orders/influencer/{influencerId}")
    @RolesAllowed({ "ADMIN" })
    public ResponseEntity<OrderAnalyticsResponse> getOrderAnalyticsByInfluencer(@PathVariable Long influencerId) {
        return ResponseEntity.ok(
                AnalyticsConverter.toResponse(
                        analyticsService.getOrderAnalyticsByInfluencer(influencerId)));
    }
}