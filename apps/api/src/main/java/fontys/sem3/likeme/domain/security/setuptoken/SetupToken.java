package fontys.sem3.likeme.domain.security.setuptoken;

import java.time.LocalDateTime;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupToken {
    private Long id;
    private InfluencerApplication application;
    private String token;
    private LocalDateTime expiresAt;
    private Boolean isUsed;
    private LocalDateTime createdOn;
}