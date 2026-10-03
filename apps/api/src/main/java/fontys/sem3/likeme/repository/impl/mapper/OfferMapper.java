package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.repository.impl.entity.offer.OfferEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfferMapper {
    public static OfferEntity mapToEntity(Offer offer) {
        if (offer == null)
            return null;

        return OfferEntity.builder()
                .id(offer.getId())
                .title(offer.getTitle())
                .description(offer.getDescription())
                .coverPhotoPath(offer.getCoverPhotoPath())
                .type(offer.getType())
                .isActive(offer.getIsActive())
                .createdOn(offer.getCreatedOn())
                .updatedOn(offer.getUpdatedOn())
                .price(offer.getPrice())
                .createdBy(InfluencerMapper.mapToEntity(offer.getCreatedBy()))
                .updatedBy(InfluencerMapper.mapToEntity(offer.getUpdatedBy()))
                .build();
    }

    public static Offer mapToDomain(OfferEntity entity) {
        if (entity == null)
            return null;

        return Offer.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .coverPhotoPath(entity.getCoverPhotoPath())
                .type(entity.getType())
                .isActive(entity.getIsActive())
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .price(entity.getPrice())
                .createdBy(InfluencerMapper.mapToDomain(entity.getCreatedBy()))
                .updatedBy(InfluencerMapper.mapToDomain(entity.getUpdatedBy()))
                .build();
    }
}