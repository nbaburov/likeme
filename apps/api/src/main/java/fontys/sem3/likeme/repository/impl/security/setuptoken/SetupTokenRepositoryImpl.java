package fontys.sem3.likeme.repository.impl.security.setuptoken;

import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.repository.interfaces.security.setuptoken.SetupTokenRepository;
import fontys.sem3.likeme.repository.impl.mapper.SetupTokenMapper;
import fontys.sem3.likeme.repository.jpa.security.setuptoken.SetupTokenRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SetupTokenRepositoryImpl implements SetupTokenRepository {
    private final SetupTokenRepositoryJPA jpaRepository;

    @Override
    public SetupToken save(SetupToken token) {
        return SetupTokenMapper.mapToDomain(
                jpaRepository.save(SetupTokenMapper.mapToEntity(token)));
    }

    @Override
    public Optional<SetupToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(SetupTokenMapper::mapToDomain);
    }

    @Override
    public Optional<SetupToken> findByApplicationId(Long applicationId) {
        return jpaRepository.findByApplicationId(applicationId)
                .map(SetupTokenMapper::mapToDomain);
    }

    @Override
    public void deleteByApplicationId(Long applicationId) {
        jpaRepository.deleteByApplicationId(applicationId);
    }
}