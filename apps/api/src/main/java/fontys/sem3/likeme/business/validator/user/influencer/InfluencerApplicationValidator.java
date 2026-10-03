package fontys.sem3.likeme.business.validator.user.influencer;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.business.exception.user.influencer.DuplicateInfluencerDataException;
import fontys.sem3.likeme.controller.dto.influencer.application.UpdateInfluencerApplicationRequest;
import fontys.sem3.likeme.repository.interfaces.influencer.InfluencerApplicationRepository;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.business.converter.user.influencer.application.InfluencerApplicationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InfluencerApplicationValidator {
    private final InfluencerApplicationRepository repository;

    public void validateCreate(InfluencerApplication application) {
        if (repository.existsByEmail(application.getEmail())) {
            throw new DuplicateInfluencerDataException("email", "An application with this email already exists");
        }
        if (repository.existsByPhoneNumber(application.getPhoneNumber())) {
            throw new DuplicateInfluencerDataException("phoneNumber",
                    "An application with this phone number already exists");
        }
        if (repository.existsByInstagramHandle(application.getInstagramHandle())) {
            throw new DuplicateInfluencerDataException("instagramHandle",
                    "An application with this Instagram handle already exists");
        }
        if (repository.existsByUsername(application.getUsername())) {
            throw new DuplicateInfluencerDataException("username", "This username is already taken");
        }
    }

    public void validateUpdate(InfluencerApplication application) {
        if (repository.existsByEmailAndIdNot(application.getEmail(), application.getId())) {
            throw new DuplicateInfluencerDataException("email", "This email is already in use");
        }
        if (repository.existsByPhoneNumberAndIdNot(application.getPhoneNumber(), application.getId())) {
            throw new DuplicateInfluencerDataException("phoneNumber", "This phone number is already in use");
        }
        if (repository.existsByInstagramHandleAndIdNot(application.getInstagramHandle(), application.getId())) {
            throw new DuplicateInfluencerDataException("instagramHandle", "This Instagram handle is already in use");
        }
        if (repository.existsByUsernameAndIdNot(application.getUsername(), application.getId())) {
            throw new DuplicateInfluencerDataException("username", "This username is already taken");
        }
    }

    public void validateUpdate(UpdateInfluencerApplicationRequest request,
            InfluencerApplication existingApplication) {
        if (request == null || (request.getUsername() == null && request.getEmail() == null
                && request.getPhoneNumber() == null && request.getAbout() == null
                && request.getInstagramHandle() == null && request.getProfilePhotoPath() == null
                && request.getCoverPhotoPath() == null && request.getIsApproved() == null
                && request.getBillingDetails() == null)) {
            throw new InvalidInfluencerDataException("Update request cannot be empty");
        }

        // Check for no changes
        InfluencerApplication updatedApplication = InfluencerApplicationConverter.toInfluencerApplication(request,
                existingApplication.getId(), existingApplication);

        if (existingApplication.equals(updatedApplication)) {
            throw new InvalidInfluencerDataException(
                    "No changes detected for influencer application with id: " + existingApplication.getId());
        }
    }
}
