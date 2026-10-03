package fontys.sem3.likeme.controller.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private String code;
    private String message;
    private String details;
    private String timestamp;
}