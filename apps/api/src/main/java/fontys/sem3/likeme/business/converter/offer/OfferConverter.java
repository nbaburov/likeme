package fontys.sem3.likeme.business.converter.offer;

import java.util.Date;

import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.offer.InvalidOfferConversionException;
import fontys.sem3.likeme.controller.dto.offer.CreateOfferRequest;
import fontys.sem3.likeme.controller.dto.offer.OfferResponse;
import fontys.sem3.likeme.controller.dto.offer.UpdateOfferRequest;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferConverter {
    public static Offer requestToDomain(CreateOfferRequest request, Influencer creator) {
        if (request == null) {
            throw new InvalidRequest("Offer request cannot be null");
        }

        try {
            return Offer.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .coverPhotoPath(request.getCoverPhotoPath())
                    .type(request.getType())
                    .isActive(true)
                    .createdBy(creator)
                    .updatedBy(creator)
                    .createdOn(new Date())
                    .updatedOn(new Date())
                    .price(request.getPrice())
                    .build();
        } catch (Exception e) {
            throw new InvalidOfferConversionException(
                    "Failed to convert create request to offer domain: " + e.getMessage(), e);
        }
    }

    public static Offer requestToDomain(UpdateOfferRequest request, Long id, Offer existingOffer, Influencer updater) {
        if (request == null || existingOffer == null) {
            throw new InvalidRequest("Update request and existing offer cannot be null");
        }

        try {
            return Offer.builder()
                    .id(id)
                    .title(request.getTitle() != null ? request.getTitle() : existingOffer.getTitle())
                    .description(request.getDescription() != null ? request.getDescription()
                            : existingOffer.getDescription())
                    .coverPhotoPath(request.getCoverPhotoPath() != null ? request.getCoverPhotoPath()
                            : existingOffer.getCoverPhotoPath())
                    .type(request.getType() != null ? request.getType() : existingOffer.getType())
                    .isActive(request.getIsActive())
                    .createdBy(existingOffer.getCreatedBy())
                    .updatedBy(updater)
                    .createdOn(existingOffer.getCreatedOn())
                    .updatedOn(new Date())
                    .price(request.getPrice() != null ? request.getPrice() : existingOffer.getPrice())
                    .build();
        } catch (Exception e) {
            throw new InvalidOfferConversionException(
                    "Failed to convert update request to offer domain: " + e.getMessage(), e);
        }
    }

    public static OfferResponse toResponse(Offer offer) {
        if (offer == null) {
            throw new InvalidRequest("Offer cannot be null");
        }

        try {
            return OfferResponse.builder()
                    .id(offer.getId())
                    .title(offer.getTitle())
                    .description(offer.getDescription())
                    .coverPhotoPath(offer.getCoverPhotoPath())
                    .type(offer.getType())
                    .isActive(offer.getIsActive())
                    .createdOn(offer.getCreatedOn())
                    .updatedOn(offer.getUpdatedOn())
                    .createdById(offer.getCreatedBy() != null ? offer.getCreatedBy().getId() : null)
                    .updatedById(offer.getUpdatedBy() != null ? offer.getUpdatedBy().getId() : null)
                    .price(offer.getPrice())
                    .build();
        } catch (Exception e) {
            throw new InvalidOfferConversionException(
                    "Failed to convert offer to response: " + e.getMessage(), e);
        }
    }
}