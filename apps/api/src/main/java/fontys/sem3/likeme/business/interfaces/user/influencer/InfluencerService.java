package fontys.sem3.likeme.business.interfaces.user.influencer;

import java.util.List;

import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;

public interface InfluencerService {
    Influencer createPendingInfluencer(InfluencerApplication application);
    Influencer completeInfluencerSetup(String token, String password);
    Influencer getInfluencerByApplicationId(Long applicationId);
    List<Influencer> getAllInfluencers();
    Influencer getInfluencerById(Long id);
    Influencer createInfluencer(Influencer influencer);
    Influencer updateInfluencer(Influencer influencer);
    void deleteInfluencer(Long id);
    boolean existsByApplicationId(Long applicationId);
    Influencer connectInstagram(Long id, String instagramAccessToken);
    Influencer getInfluencerUsername(String username);
}
