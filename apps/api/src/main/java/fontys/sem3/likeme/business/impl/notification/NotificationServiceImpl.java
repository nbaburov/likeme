package fontys.sem3.likeme.business.impl.notification;

import fontys.sem3.likeme.business.exception.notification.InvalidNotificationDataException;
import fontys.sem3.likeme.business.exception.notification.NotificationServiceException;
import fontys.sem3.likeme.business.exception.notification.WebSocketConnectionException;
import fontys.sem3.likeme.business.interfaces.notification.NotificationService;
import fontys.sem3.likeme.controller.dto.notification.Notification;
import fontys.sem3.likeme.controller.dto.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendInvoicePaidNotification(Long influencerId, Long invoiceId) {
        try {
            validateIds(influencerId, invoiceId, "Influencer ID and Invoice ID cannot be null");
            Notification notification = createInvoicePaidNotification(influencerId, invoiceId);
            sendNotificationToInfluencer(influencerId, notification);
        } catch (InvalidNotificationDataException | WebSocketConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new NotificationServiceException("Failed to process notification: " + e.getMessage());
        }
    }

    @Override
    public void sendOrderCompletedNotification(Long clientId, Long orderId) {
        try {
            validateIds(clientId, orderId, "Client ID and Order ID cannot be null");
            Notification notification = createOrderCompletedNotification(clientId, orderId);
            sendNotificationToClient(clientId, notification);
        } catch (InvalidNotificationDataException | WebSocketConnectionException e) {
            throw e;
        } catch (Exception e) {
            throw new NotificationServiceException("Failed to process notification: " + e.getMessage());
        }
    }

    private void validateIds(Long id1, Long id2, String errorMessage) {
        if (id1 == null || id2 == null) {
            throw new InvalidNotificationDataException(errorMessage);
        }
    }

    private Notification createInvoicePaidNotification(Long influencerId, Long invoiceId) {
        return Notification.builder()
                .type(NotificationType.INVOICE_PAID)
                .message("Invoice #" + invoiceId + " has been paid")
                .userId(influencerId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Notification createOrderCompletedNotification(Long clientId, Long orderId) {
        return Notification.builder()
                .type(NotificationType.ORDER_COMPLETED)
                .message("Order #" + orderId + " has been completed")
                .userId(clientId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void sendNotificationToInfluencer(Long influencerId, Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    influencerId.toString(),
                    "/influencer/notifications",
                    notification);
        } catch (Exception e) {
            throw new WebSocketConnectionException("Failed to send notification: " + e.getMessage());
        }
    }

    private void sendNotificationToClient(Long clientId, Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    clientId.toString(),
                    "/client/notifications",
                    notification);
        } catch (Exception e) {
            throw new WebSocketConnectionException("Failed to send notification: " + e.getMessage());
        }
    }
}