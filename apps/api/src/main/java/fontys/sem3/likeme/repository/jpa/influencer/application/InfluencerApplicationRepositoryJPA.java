package fontys.sem3.likeme.repository.jpa.influencer.application;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerApplicationEntity;

// ? Is this good practice?

@Repository
public interface InfluencerApplicationRepositoryJPA extends JpaRepository<InfluencerApplicationEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByInstagramHandle(String instagramHandle);

    boolean existsByUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByInstagramHandleAndIdNot(String instagramHandle, Long id);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Optional<InfluencerApplicationEntity> findByUsername(String username);
}
