package fontys.sem3.likeme.business.interfaces.security.setuptoken;

import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;

public interface SetupTokenService {
    SetupToken createToken(InfluencerApplication application);
    SetupToken validateToken(String token);
    void invalidateToken(String token);
    void deleteTokenByApplicationId(Long applicationId);
}