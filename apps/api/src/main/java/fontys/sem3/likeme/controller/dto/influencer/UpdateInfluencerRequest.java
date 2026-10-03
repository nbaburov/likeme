package fontys.sem3.likeme.controller.dto.influencer;

import fontys.sem3.likeme.controller.dto.influencer.application.UpdateInfluencerApplicationRequest;
import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInfluencerRequest {
    @Valid
    private UpdateInfluencerApplicationRequest application;
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    private Boolean isInstagramConnected;
    private String instagramAccessToken;
    private InfluencerStatus status;
    private Boolean isActive;
}
