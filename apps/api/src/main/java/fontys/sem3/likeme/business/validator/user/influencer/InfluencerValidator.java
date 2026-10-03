package fontys.sem3.likeme.business.validator.user.influencer;

import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.business.converter.user.influencer.InfluencerConverter;
import fontys.sem3.likeme.business.validator.security.PasswordValidator;
import fontys.sem3.likeme.controller.dto.influencer.UpdateInfluencerRequest;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InfluencerValidator {
    private final InfluencerRepository influencerRepository;
    private final PasswordValidator passwordValidator;

    public void validateCreate(Influencer influencer) {
        if (influencer.getApplication() == null) {
            throw new InvalidInfluencerDataException("Application cannot be null");
        }

        // Validate unique username
        if (influencerRepository.existsByApplicationUsername(influencer.getApplication().getUsername())) {
            throw new DuplicateInfluencerDataException("username", "Username already exists");
        }

        // Validate unique email
        if (influencerRepository.existsByApplicationEmail(influencer.getApplication().getEmail())) {
            throw new DuplicateInfluencerDataException("email", "Email already exists");
        }

        // Validate unique phone number
        if (influencerRepository.existsByApplicationPhoneNumber(influencer.getApplication().getPhoneNumber())) {
            throw new DuplicateInfluencerDataException("phoneNumber", "Phone number already exists");
        }

        // Validate unique Instagram handle
        if (influencerRepository.existsByApplicationInstagramHandle(influencer.getApplication().getInstagramHandle())) {
            throw new DuplicateInfluencerDataException("instagramHandle", "Instagram handle already exists");
        }

    }

    public void validateUpdate(Influencer influencer) {
        if (influencer.getId() == null) {
            throw new InvalidInfluencerDataException("Influencer ID cannot be null for update");
        }

        influencerRepository.findById(influencer.getId()).ifPresent(existingInfluencer -> {
            if (existingInfluencer.equals(influencer)) {
                throw new InvalidInfluencerDataException(
                        "No changes detected for influencer with id: " + influencer.getId());
            }

            // Validate unique username
            if (!existingInfluencer.getApplication().getUsername().equals(influencer.getApplication().getUsername())
                    && influencerRepository.existsByApplicationUsername(influencer.getApplication().getUsername())) {
                throw new DuplicateInfluencerDataException("username", "Username already exists");
            }

            // Validate unique email
            if (!existingInfluencer.getApplication().getEmail().equals(influencer.getApplication().getEmail())
                    && influencerRepository.existsByApplicationEmail(influencer.getApplication().getEmail())) {
                throw new DuplicateInfluencerDataException("email", "Email already exists");
            }

            // Validate unique phone number
            if (!existingInfluencer.getApplication().getPhoneNumber()
                    .equals(influencer.getApplication().getPhoneNumber())
                    && influencerRepository
                            .existsByApplicationPhoneNumber(influencer.getApplication().getPhoneNumber())) {
                throw new DuplicateInfluencerDataException("phoneNumber", "Phone number already exists");
            }

            // Validate unique Instagram handle
            if (!existingInfluencer.getApplication().getInstagramHandle()
                    .equals(influencer.getApplication().getInstagramHandle())
                    && influencerRepository
                            .existsByApplicationInstagramHandle(influencer.getApplication().getInstagramHandle())) {
                throw new DuplicateInfluencerDataException("instagramHandle", "Instagram handle already exists");
            }
        });

    }

    public void validateUpdate(UpdateInfluencerRequest request, Influencer existingInfluencer) {
        if (request == null || (request.getApplication() == null &&
                request.getPassword() == null &&
                request.getInstagramAccessToken() == null &&
                request.getStatus() == null &&
                request.getIsInstagramConnected() == null)) {
            throw new InvalidInfluencerDataException("Update request cannot be empty");
        }

        Influencer updatedInfluencer = InfluencerConverter.toInfluencer(request, existingInfluencer.getId(),
                existingInfluencer);
        if (existingInfluencer.equals(updatedInfluencer)
                && passwordValidator.comparePasswords(request.getPassword(), existingInfluencer.getPassword(),
                        existingInfluencer.getSalt())) {
            throw new InvalidInfluencerDataException(
                    "No changes detected for influencer with id: " + existingInfluencer.getId());
        }
    }

    public void validateInstagramConnection(Influencer influencer) {
        if (influencer.getInstagramAccessToken() == null || influencer.getInstagramAccessToken().trim().isEmpty()) {
            throw new InvalidInfluencerDataException("Instagram access token cannot be empty");
        }

        if (influencer.getStatus() != InfluencerStatus.PENDING_INSTAGRAM) {
            throw new InvalidInfluencerDataException("Invalid influencer status for Instagram connection");
        }
    }
}
