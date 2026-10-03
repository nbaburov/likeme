package fontys.sem3.likeme.repository.interfaces.security.setuptoken;

import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import java.util.Optional;

public interface SetupTokenRepository {
    SetupToken save(SetupToken token);
    Optional<SetupToken> findByToken(String token);
    Optional<SetupToken> findByApplicationId(Long applicationId);
    void deleteByApplicationId(Long applicationId);
}