package fontys.sem3.likeme.repository.jpa.influencer;

import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InfluencerRepositoryJPA extends JpaRepository<InfluencerEntity, Long> {
    Optional<InfluencerEntity> findByApplicationId(Long applicationId);

    boolean existsByApplicationId(Long applicationId);

    boolean existsByApplicationUsername(String username);

    boolean existsByApplicationEmail(String email);

    boolean existsByApplicationPhoneNumber(String phoneNumber);

    boolean existsByApplicationInstagramHandle(String instagramHandle);
}
