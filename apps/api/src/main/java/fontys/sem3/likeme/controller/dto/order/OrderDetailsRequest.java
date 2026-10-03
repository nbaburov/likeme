package fontys.sem3.likeme.controller.dto.order;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsRequest {
    private String postId;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;
}