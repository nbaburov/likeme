package fontys.sem3.likeme.business.interfaces.user.influencer;

import java.util.List;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;

public interface InfluencerApplicationService {
    List<InfluencerApplication> getInfluencerApplications();
    InfluencerApplication getInfluencerApplicationById(Long id) throws InfluencerNotFoundException;
    InfluencerApplication createInfluencerApplication(InfluencerApplication application) throws InvalidInfluencerDataException;
    InfluencerApplication updateInfluencerApplication(InfluencerApplication application) throws InfluencerNotFoundException, InvalidInfluencerDataException;
    void deleteInfluencerApplication(Long id) throws InfluencerNotFoundException;
}