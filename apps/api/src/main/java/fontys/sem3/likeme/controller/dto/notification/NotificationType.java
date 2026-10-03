package fontys.sem3.likeme.controller.dto.notification;

public enum NotificationType {
    INVOICE_PAID("Invoice paid"),
    ORDER_COMPLETED("Order completed");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}