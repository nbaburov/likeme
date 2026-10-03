package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.repository.impl.entity.security.setuptoken.SetupTokenEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SetupTokenMapper {
    public static SetupToken mapToDomain(SetupTokenEntity entity) {
        if (entity == null)
            return null;

        return SetupToken.builder()
                .id(entity.getId())
                .application(InfluencerApplicationMapper.mapToDomainModel(entity.getApplication()))
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .isUsed(entity.getIsUsed())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static SetupTokenEntity mapToEntity(SetupToken domain) {
        if (domain == null)
            return null;

        return SetupTokenEntity.builder()
                .id(domain.getId())
                .application(InfluencerApplicationMapper.mapToEntity(domain.getApplication()))
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .isUsed(domain.getIsUsed())
                .createdOn(domain.getCreatedOn())
                .build();
    }
}