package fontys.sem3.likeme.business.impl.platform;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fontys.sem3.likeme.business.exception.platform.InvalidPlatformSettingsException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsNotFoundException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsServiceException;
import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.interfaces.platform.PlatformSettingsService;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.validator.platform.PlatformSettingsValidator;
import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.platform.PlatformSettings;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.platform.PlatformSettingsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
    private final PlatformSettingsRepository platformSettingsRepository;
    private final AdminService adminService;
    private final PlatformSettingsValidator validator;

    @Override
    public List<PlatformSettings> getAllSettings() {
        try {
            return platformSettingsRepository.findAll();
        } catch (Exception e) {
            throw new PlatformSettingsServiceException("Failed to retrieve all platform settings", e);
        }
    }

    @Override
    public PlatformSettings getSettingByType(PlatformSettingType type) {
        try {
            return platformSettingsRepository.findByType(type)
                    .orElseThrow(() -> new PlatformSettingsNotFoundException(
                            "Platform settings not found for type: " + type));
        } catch (PlatformSettingsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlatformSettingsServiceException("Failed to retrieve platform settings", e);
        }
    }

    @Override
    @Transactional
    public PlatformSettings updateSetting(Long adminId, PlatformSettingType type, String value) {
        if (adminId == null) {
            throw new InvalidPlatformSettingsException("Admin ID cannot be null");
        }
        if (type == null) {
            throw new InvalidPlatformSettingsException("Setting type cannot be null");
        }
        if (value == null) {
            throw new InvalidPlatformSettingsException("Setting value cannot be null");
        }

        try {
            validator.validateSettingValue(type, value);
            Admin admin = adminService.getAdminById(adminId);

            PlatformSettings setting = platformSettingsRepository.findByType(type)
                    .orElse(PlatformSettings.builder()
                            .type(type)
                            .build());

            setting.setValue(value);
            setting.setUpdatedBy(admin);
            setting.setUpdatedOn(new Date());

            return platformSettingsRepository.save(setting);

        } catch (InvalidPlatformSettingsException | AdminNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlatformSettingsServiceException(
                    "Failed to update setting " + type + " to " + value, e);
        }
    }
}