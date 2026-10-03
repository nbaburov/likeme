package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InfluencerMapper {
    public static Influencer mapToDomain(InfluencerEntity entity) {
        return Influencer.builder()
                .id(entity.getId())
                .application(InfluencerApplicationMapper.mapToDomainModel(entity.getApplication()))
                .password(entity.getPassword())
                .salt(entity.getSalt())
                .role(Role.INFLUENCER)
                .isInstagramConnected(entity.getIsInstagramConnected())
                .instagramAccessToken(entity.getInstagramAccessToken())
                .isActive(entity.getIsActive())
                .status(entity.getStatus())
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .lastLoginOn(entity.getLastLoginOn())
                .build();

    }

    public static InfluencerEntity mapToEntity(Influencer domain) {
        return InfluencerEntity.builder()
                .id(domain.getId())
                .application(InfluencerApplicationMapper.mapToEntity(domain.getApplication()))
                .password(domain.getPassword())
                .salt(domain.getSalt())
                .isInstagramConnected(domain.getIsInstagramConnected())
                .instagramAccessToken(domain.getInstagramAccessToken())
                .status(domain.getStatus())
                .isActive(domain.getIsActive())
                .createdOn(domain.getCreatedOn())
                .updatedOn(domain.getUpdatedOn())
                .lastLoginOn(domain.getLastLoginOn())
                .build();
    }
}