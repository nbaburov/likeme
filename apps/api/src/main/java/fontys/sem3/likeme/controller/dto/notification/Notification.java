package fontys.sem3.likeme.controller.dto.notification;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class Notification {
    @NotNull(message = "Message is required")
    private String message;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private NotificationType type;
    private LocalDateTime timestamp;
    private Object data;
}

