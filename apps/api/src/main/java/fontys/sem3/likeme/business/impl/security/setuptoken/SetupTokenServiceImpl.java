package fontys.sem3.likeme.business.impl.security.setuptoken;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupTokenException;
import fontys.sem3.likeme.business.exception.security.setuptoken.SetupTokenServiceException;
import fontys.sem3.likeme.business.interfaces.security.setuptoken.SetupTokenService;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.interfaces.security.setuptoken.SetupTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SetupTokenServiceImpl implements SetupTokenService {
    private final SetupTokenRepository setupTokenRepository;

    @Override
    public SetupToken createToken(InfluencerApplication application) {
        if (application == null || application.getId() == null) {
            throw new InvalidSetupTokenException("Invalid application data");
        }
        try {
            SetupToken token = SetupToken.builder()
                    .application(application)
                    .token(UUID.randomUUID().toString())
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .isUsed(false)
                    .build();
            return setupTokenRepository.save(token);
        } catch (Exception e) {
            throw new SetupTokenServiceException("Failed to create setup token", e);
        }
    }

    @Override
    public SetupToken validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new InvalidSetupTokenException("Token cannot be null or empty");
        }

        try {
            SetupToken setupToken = setupTokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidSetupTokenException("Token not found or invalid"));

            // Eager validation to prevent lazy loading issues
            if (setupToken.getApplication() == null || setupToken.getApplication().getId() == null) {
                throw new InvalidSetupTokenException("Token is not associated with a valid application");
            }

            if (setupToken.getExpiresAt() == null || setupToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new InvalidSetupTokenException("Token has expired");
            }

            if (Boolean.TRUE.equals(setupToken.getIsUsed())) {
                throw new InvalidSetupTokenException("Token has already been used");
            }

            return setupToken;
        } catch (InvalidSetupTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new SetupTokenServiceException("Error validating setup token", e);
        }
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null) {
            throw new InvalidSetupTokenException("Token cannot be null");
        }
        try {
            SetupToken setupToken = setupTokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidSetupTokenException("Token not found"));

            if (setupToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new InvalidSetupTokenException("Token has expired");
            }

            if (Boolean.TRUE.equals(setupToken.getIsUsed())) {
                throw new InvalidSetupTokenException("Token is already used");
            }

            setupToken.setIsUsed(true);
            setupTokenRepository.save(setupToken);
        } catch (InvalidSetupTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new SetupTokenServiceException("Failed to invalidate setup token", e);
        }
    }

    @Override
    public void deleteTokenByApplicationId(Long applicationId) {
        if (applicationId == null) {
            throw new InvalidSetupTokenException("Application ID cannot be null");
        }
        try {
            setupTokenRepository.deleteByApplicationId(applicationId);
        } catch (Exception e) {
            throw new SetupTokenServiceException("Failed to delete setup token by application ID", e);
        }
    }
}