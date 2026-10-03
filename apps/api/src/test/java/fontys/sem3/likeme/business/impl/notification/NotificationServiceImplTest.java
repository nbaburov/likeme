package fontys.sem3.likeme.business.impl.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import fontys.sem3.likeme.business.exception.notification.InvalidNotificationDataException;
import fontys.sem3.likeme.business.exception.notification.WebSocketConnectionException;
import fontys.sem3.likeme.controller.dto.notification.Notification;
import fontys.sem3.likeme.controller.dto.notification.NotificationType;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    private static final Long INFLUENCER_ID = 1L;
    private static final Long CLIENT_ID = 2L;
    private static final Long INVOICE_ID = 100L;
    private static final Long ORDER_ID = 200L;

    @Test
    void sendInvoicePaidNotification_Success() {
        notificationService.sendInvoicePaidNotification(INFLUENCER_ID, INVOICE_ID);

        verify(messagingTemplate).convertAndSendToUser(
                eq(INFLUENCER_ID.toString()),
                eq("/influencer/notifications"),
                notificationCaptor.capture()
        );

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(NotificationType.INVOICE_PAID, capturedNotification.getType());
        assertEquals("Invoice #" + INVOICE_ID + " has been paid", capturedNotification.getMessage());
        assertEquals(INFLUENCER_ID, capturedNotification.getUserId());
        assertNotNull(capturedNotification.getTimestamp());
        assertNull(capturedNotification.getData());
    }

    @Test
    void sendInvoicePaidNotification_NullInfluencerId_ThrowsInvalidNotificationDataException() {
        InvalidNotificationDataException exception = assertThrows(
                InvalidNotificationDataException.class,
                () -> notificationService.sendInvoicePaidNotification(null, INVOICE_ID)
        );

        assertEquals("Influencer ID and Invoice ID cannot be null", exception.getMessage());
        verify(messagingTemplate, never()).convertAndSendToUser(
                eq(INFLUENCER_ID.toString()), 
                eq("/influencer/notifications"), 
                any(Notification.class)
        );
    }

    @Test
    void sendInvoicePaidNotification_NullInvoiceId_ThrowsInvalidNotificationDataException() {
        InvalidNotificationDataException exception = assertThrows(
                InvalidNotificationDataException.class,
                () -> notificationService.sendInvoicePaidNotification(INFLUENCER_ID, null)
        );

        assertEquals("Influencer ID and Invoice ID cannot be null", exception.getMessage());
        verify(messagingTemplate, never()).convertAndSendToUser(
                eq(INFLUENCER_ID.toString()), 
                eq("/influencer/notifications"), 
                any(Notification.class)
        );
    }

    @Test
    void sendInvoicePaidNotification_WebSocketError_ThrowsWebSocketConnectionException() {
        doThrow(new RuntimeException("Connection error"))
                .when(messagingTemplate)
                .convertAndSendToUser(
                        eq(INFLUENCER_ID.toString()),
                        eq("/influencer/notifications"),
                        any(Notification.class)
                );

        WebSocketConnectionException exception = assertThrows(
                WebSocketConnectionException.class,
                () -> notificationService.sendInvoicePaidNotification(INFLUENCER_ID, INVOICE_ID)
        );

        assertEquals("Failed to send notification: Connection error", exception.getMessage());
    }

    @Test
    void sendOrderCompletedNotification_Success() {
        notificationService.sendOrderCompletedNotification(CLIENT_ID, ORDER_ID);

        verify(messagingTemplate).convertAndSendToUser(
                eq(CLIENT_ID.toString()),
                eq("/client/notifications"),
                notificationCaptor.capture()
        );

        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(NotificationType.ORDER_COMPLETED, capturedNotification.getType());
        assertEquals("Order #" + ORDER_ID + " has been completed", capturedNotification.getMessage());
        assertEquals(CLIENT_ID, capturedNotification.getUserId());
        assertNotNull(capturedNotification.getTimestamp());
        assertNull(capturedNotification.getData());
    }

    @Test
    void sendOrderCompletedNotification_NullClientId_ThrowsInvalidNotificationDataException() {
        InvalidNotificationDataException exception = assertThrows(
                InvalidNotificationDataException.class,
                () -> notificationService.sendOrderCompletedNotification(null, ORDER_ID)
        );

        assertEquals("Client ID and Order ID cannot be null", exception.getMessage());
        verify(messagingTemplate, never()).convertAndSendToUser(
                eq(CLIENT_ID.toString()), 
                eq("/client/notifications"), 
                any(Notification.class)
        );
    }

    @Test
    void sendOrderCompletedNotification_NullOrderId_ThrowsInvalidNotificationDataException() {
        InvalidNotificationDataException exception = assertThrows(
                InvalidNotificationDataException.class,
                () -> notificationService.sendOrderCompletedNotification(CLIENT_ID, null)
        );

        assertEquals("Client ID and Order ID cannot be null", exception.getMessage());
        verify(messagingTemplate, never()).convertAndSendToUser(
                eq(CLIENT_ID.toString()), 
                eq("/client/notifications"), 
                any(Notification.class)
        );
    }

    @Test
    void sendOrderCompletedNotification_WebSocketError_ThrowsWebSocketConnectionException() {
        doThrow(new RuntimeException("Connection error"))
                .when(messagingTemplate)
                .convertAndSendToUser(
                        eq(CLIENT_ID.toString()),
                        eq("/client/notifications"),
                        any(Notification.class)
                );

        WebSocketConnectionException exception = assertThrows(
                WebSocketConnectionException.class,
                () -> notificationService.sendOrderCompletedNotification(CLIENT_ID, ORDER_ID)
        );

        assertEquals("Failed to send notification: Connection error", exception.getMessage());
    }
}