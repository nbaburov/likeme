package fontys.sem3.likeme.business.validator.offer;

import fontys.sem3.likeme.business.exception.offer.InvalidOfferDataException;
import fontys.sem3.likeme.business.converter.offer.OfferConverter;
import fontys.sem3.likeme.controller.dto.offer.UpdateOfferRequest;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.interfaces.offer.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferValidator {
    private final OfferRepository offerRepository;

    public void validateCreate(Offer offer) {

        if (offer.getCreatedBy() == null) {
            throw new InvalidOfferDataException("Offer must have a creator");
        }

    }

    public void validateUpdate(UpdateOfferRequest request, Offer existingOffer, Influencer updater) {
        if (request == null || (request.getTitle() == null && request.getDescription() == null
                && request.getCoverPhotoPath() == null && request.getType() == null)) {
            throw new InvalidOfferDataException("Update request cannot be empty");
        }

        // Check if user has permission to update
        if (!updater.getRole().equals(Role.ADMIN) && 
            !existingOffer.getCreatedBy().getId().equals(updater.getId())) {
            throw new InvalidOfferDataException("You don't have permission to update this offer");
        }

        // Check for no changes
        Offer updatedOffer = OfferConverter.requestToDomain(request, existingOffer.getId(), existingOffer, updater);
        if (existingOffer.equals(updatedOffer)) {
            throw new InvalidOfferDataException("No changes detected for offer with id: " + existingOffer.getId());
        }
    }
}