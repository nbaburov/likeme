package fontys.sem3.likeme.controller.dto.influencer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectInstagramRequest {
    @NotBlank(message = "Instagram access token is required")
    private String instagramAccessToken;
}
