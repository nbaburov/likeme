package fontys.sem3.likeme.repository.interfaces.influencer;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;

import java.util.List;
import java.util.Optional;

public interface InfluencerApplicationRepository {
    List<InfluencerApplication> findAll();
    Optional<InfluencerApplication> findById(Long id);
    Optional<InfluencerApplication> findByUsername(String username);
    InfluencerApplication save(InfluencerApplication application);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByInstagramHandle(String instagramHandle);
    boolean existsByUsername(String username);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
    boolean existsByInstagramHandleAndIdNot(String instagramHandle, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);
}
