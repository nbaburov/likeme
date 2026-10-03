package fontys.sem3.likeme.repository.jpa.security.setuptoken;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.repository.impl.entity.security.setuptoken.SetupTokenEntity;

@Repository
public interface SetupTokenRepositoryJPA extends JpaRepository<SetupTokenEntity, Long> {
    Optional<SetupTokenEntity> findByToken(String token);

    Optional<SetupTokenEntity> findByApplicationId(Long applicationId);

    void deleteByApplicationId(Long applicationId);
}