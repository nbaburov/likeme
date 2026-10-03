package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerApplicationEntity;
import static fontys.sem3.likeme.repository.impl.mapper.BillingDetailsMapper.mapToDomainBillingDetails;
import static fontys.sem3.likeme.repository.impl.mapper.BillingDetailsMapper.mapToEntityBillingDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InfluencerApplicationMapper {

    public static InfluencerApplication mapToDomainModel(InfluencerApplicationEntity entity) {
        if (entity == null)
            return null;

        return InfluencerApplication.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .about(entity.getAbout())
                .instagramHandle(entity.getInstagramHandle())
                .isApproved(entity.getIsApproved())
                .billingDetails(mapToDomainBillingDetails(entity.getBillingDetails()))
                .profilePhotoPath(entity.getProfilePhotoPath())
                .coverPhotoPath(entity.getCoverPhotoPath())
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .build();

    }

    public static InfluencerApplicationEntity mapToEntity(InfluencerApplication domain) {
        if (domain == null)
            return null;

        return InfluencerApplicationEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .about(domain.getAbout())
                .instagramHandle(domain.getInstagramHandle())
                .isApproved(domain.getIsApproved())
                .billingDetails(mapToEntityBillingDetails(domain.getBillingDetails()))
                .profilePhotoPath(domain.getProfilePhotoPath())
                .coverPhotoPath(domain.getCoverPhotoPath())
                .createdOn(domain.getCreatedOn())
                .updatedOn(domain.getUpdatedOn())
                .build();
    }
}