package fontys.sem3.likeme.business.interfaces.notification;

public interface NotificationService {
    void sendInvoicePaidNotification(Long influencerId, Long invoiceId);
    void sendOrderCompletedNotification(Long clientId, Long orderId);
}
