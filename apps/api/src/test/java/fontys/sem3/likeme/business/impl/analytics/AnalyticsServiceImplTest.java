package fontys.sem3.likeme.business.impl.analytics;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.analytics.AnalyticsException;
import fontys.sem3.likeme.business.exception.analytics.InvalidAnalyticsRequestException;
import fontys.sem3.likeme.domain.analytics.OrderAnalytics;
import fontys.sem3.likeme.repository.interfaces.order.OrderRepository;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private static final Long INFLUENCER_ID = 1L;
    private Map<String, Long> testOrdersByCountry;

    @BeforeEach
    void setUp() {
        testOrdersByCountry = new HashMap<>();
        testOrdersByCountry.put("Netherlands", 5L);
        testOrdersByCountry.put("Germany", 3L);
        testOrdersByCountry.put("Belgium", 2L);
    }

    @Test
    void getOrderAnalyticsByInfluencer_Success() {
        when(orderRepository.findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID))
                .thenReturn(testOrdersByCountry);

        OrderAnalytics result = analyticsService.getOrderAnalyticsByInfluencer(INFLUENCER_ID);

        assertNotNull(result);
        assertEquals(INFLUENCER_ID, result.getInfluencerId());
        assertEquals(testOrdersByCountry, result.getOrdersByCountry());
        assertEquals(3, result.getOrdersByCountry().size());
        assertEquals(5L, result.getOrdersByCountry().get("Netherlands"));
        assertEquals(3L, result.getOrdersByCountry().get("Germany"));
        assertEquals(2L, result.getOrdersByCountry().get("Belgium"));

        verify(orderRepository).findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderAnalyticsByInfluencer_NullId_ThrowsException() {
        InvalidAnalyticsRequestException exception = assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> analyticsService.getOrderAnalyticsByInfluencer(null));

        assertEquals("Influencer ID cannot be null", exception.getMessage());
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderAnalyticsByInfluencer_NoOrders_ThrowsException() {
        when(orderRepository.findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID))
                .thenReturn(new HashMap<>());

        InvalidAnalyticsRequestException exception = assertThrows(
                InvalidAnalyticsRequestException.class,
                () -> analyticsService.getOrderAnalyticsByInfluencer(INFLUENCER_ID));

        assertEquals("No orders found for influencer with ID: " + INFLUENCER_ID,
                exception.getMessage());
        verify(orderRepository).findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderAnalyticsByInfluencer_RepositoryError_ThrowsException() {
        when(orderRepository.findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID))
                .thenThrow(new RuntimeException("Database error"));

        AnalyticsException exception = assertThrows(
                AnalyticsException.class,
                () -> analyticsService.getOrderAnalyticsByInfluencer(INFLUENCER_ID));

        assertEquals("Failed to generate analytics for influencer: Database error",
                exception.getMessage());
        verify(orderRepository).findOrdersByInfluencerGroupedByClientCountry(INFLUENCER_ID);
        verifyNoMoreInteractions(orderRepository);
    }
}