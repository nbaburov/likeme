package fontys.sem3.likeme.controller.dto.influencer;

import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import fontys.sem3.likeme.controller.dto.influencer.application.CreateInfluencerApplicationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInfluencerRequest {
    @Valid
    private CreateInfluencerApplicationRequest application;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    @Default
    private Boolean isInstagramConnected = false;
    private String instagramAccessToken;
    @Default
    private InfluencerStatus status = InfluencerStatus.PENDING_INSTAGRAM;
}
