package fontys.sem3.likeme.repository.impl.influencer.application;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;
import fontys.sem3.likeme.repository.impl.mapper.InfluencerApplicationMapper;
import fontys.sem3.likeme.repository.jpa.influencer.application.InfluencerApplicationRepositoryJPA;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InfluencerApplicationRepositoryImpl implements InfluencerApplicationRepository {
    private final InfluencerApplicationRepositoryJPA jpaRepository;

    @Override
    public List<InfluencerApplication> findAll() {
        return jpaRepository.findAll().stream()
                .map(InfluencerApplicationMapper::mapToDomainModel)
                .toList();
    }

    @Override
    public Optional<InfluencerApplication> findById(Long id) {
        return jpaRepository.findById(id).map(InfluencerApplicationMapper::mapToDomainModel);
    }

    @Override
    public Optional<InfluencerApplication> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(InfluencerApplicationMapper::mapToDomainModel);
    }

    @Override
    @Transactional
    public InfluencerApplication save(InfluencerApplication application) {
        var entity = InfluencerApplicationMapper.mapToEntity(application);
        entity = jpaRepository.save(entity);
        return InfluencerApplicationMapper.mapToDomainModel(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return jpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByInstagramHandle(String instagramHandle) {
        return jpaRepository.existsByInstagramHandle(instagramHandle);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id) {
        return jpaRepository.existsByPhoneNumberAndIdNot(phoneNumber, id);
    }

    @Override
    public boolean existsByInstagramHandleAndIdNot(String instagramHandle, Long id) {
        return jpaRepository.existsByInstagramHandleAndIdNot(instagramHandle, id);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return jpaRepository.existsByUsernameAndIdNot(username, id);
    }
}
