package fontys.sem3.likeme.repository.impl.platform;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.platform.PlatformSettings;
import fontys.sem3.likeme.repository.impl.mapper.PlatformSettingsMapper;
import fontys.sem3.likeme.repository.interfaces.platform.PlatformSettingsRepository;
import fontys.sem3.likeme.repository.jpa.platform.PlatformSettingsRepositoryJPA;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlatformSettingsRepositoryImpl implements PlatformSettingsRepository {
    private final PlatformSettingsRepositoryJPA jpaRepository;

    @Override
    public PlatformSettings save(PlatformSettings settings) {
        return PlatformSettingsMapper.mapToDomain(
                jpaRepository.save(PlatformSettingsMapper.mapToEntity(settings)));
    }

    @Override
    public Optional<PlatformSettings> findByType(PlatformSettingType type) {
        return jpaRepository.findByType(type)
                .map(PlatformSettingsMapper::mapToDomain);
    }

    @Override
    public List<PlatformSettings> findAll() {
        return jpaRepository.findAll().stream()
                .map(PlatformSettingsMapper::mapToDomain)
                .toList();
    }
}