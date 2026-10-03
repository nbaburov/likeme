package fontys.sem3.likeme.repository.interfaces.influencer;

import fontys.sem3.likeme.domain.user.influencer.Influencer;
import java.util.List;
import java.util.Optional;

public interface InfluencerRepository {
    Influencer save(Influencer influencer);
    Optional<Influencer> findByApplicationId(Long applicationId);
    Optional<Influencer> findById(Long id);
    List<Influencer> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByApplicationId(Long applicationId);  boolean existsByApplicationUsername(String username);
    boolean existsByApplicationEmail(String email);
    boolean existsByApplicationPhoneNumber(String phoneNumber);
    boolean existsByApplicationInstagramHandle(String instagramHandle);
}
