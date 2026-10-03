package fontys.sem3.likeme.repository.impl.influencer;

import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerRepository;
import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerEntity;
import fontys.sem3.likeme.repository.impl.mapper.InfluencerMapper;
import fontys.sem3.likeme.repository.jpa.influencer.InfluencerRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InfluencerRepositoryImpl implements InfluencerRepository {
    private final InfluencerRepositoryJPA jpaRepository;

    @Override
    public List<Influencer> findAll() {
        return jpaRepository.findAll().stream()
                .map(InfluencerMapper::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Influencer> findById(Long id) {
        return jpaRepository.findById(id)
                .map(InfluencerMapper::mapToDomain);
    }

    @Override
    public Influencer save(Influencer influencer) {
        InfluencerEntity entity = InfluencerMapper.mapToEntity(influencer);
        return InfluencerMapper.mapToDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Influencer> findByApplicationId(Long applicationId) {
        return jpaRepository.findByApplicationId(applicationId)
                .map(InfluencerMapper::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByApplicationId(Long applicationId) {
        return jpaRepository.existsByApplicationId(applicationId);
    }

    @Override
    public boolean existsByApplicationUsername(String username) {
        return jpaRepository.existsByApplicationUsername(username);
    }

    @Override
    public boolean existsByApplicationEmail(String email) {
        return jpaRepository.existsByApplicationEmail(email);
    }

    @Override
    public boolean existsByApplicationPhoneNumber(String phoneNumber) {
        return jpaRepository.existsByApplicationPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByApplicationInstagramHandle(String instagramHandle) {
        return jpaRepository.existsByApplicationInstagramHandle(instagramHandle);
    }
}
