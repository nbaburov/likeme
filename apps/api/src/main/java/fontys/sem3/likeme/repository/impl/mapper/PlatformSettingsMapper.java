package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.platform.PlatformSettings;
import fontys.sem3.likeme.repository.impl.entity.platform.PlatformSettingsEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformSettingsMapper {
    public static PlatformSettings mapToDomain(PlatformSettingsEntity entity) {
        if (entity == null) return null;
        
        return PlatformSettings.builder()
                .id(entity.getId())
                .type(entity.getType())
                .value(entity.getValue())
                .updatedOn(entity.getUpdatedOn())
                .updatedBy(entity.getUpdatedBy() != null ? AdminMapper.mapToDomain(entity.getUpdatedBy()) : null)
                .build();
    }

    public static PlatformSettingsEntity mapToEntity(PlatformSettings domain) {
        if (domain == null) return null;

        return PlatformSettingsEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .value(domain.getValue())
                .updatedOn(domain.getUpdatedOn())
                .updatedBy(domain.getUpdatedBy() != null ? AdminMapper.mapToEntity(domain.getUpdatedBy()) : null)
                .build();
    }
}