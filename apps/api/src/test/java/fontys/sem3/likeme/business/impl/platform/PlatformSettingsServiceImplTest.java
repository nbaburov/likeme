package fontys.sem3.likeme.business.impl.platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.platform.InvalidPlatformSettingsException;
import fontys.sem3.likeme.business.exception.platform.PlatformSettingsNotFoundException;
import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.validator.platform.PlatformSettingsValidator;
import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.platform.PlatformSettings;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.platform.PlatformSettingsRepository;

@ExtendWith(MockitoExtension.class)
class PlatformSettingsServiceImplTest {
    @Mock
    private PlatformSettingsRepository platformSettingsRepository;
    @Mock
    private AdminService adminService;
    @Mock
    private PlatformSettingsValidator validator;

    @InjectMocks
    private PlatformSettingsServiceImpl platformSettingsService;

    private static final Long ADMIN_ID = 1L;
    private static final Long SETTING_ID = 1L;
    private static final String VALID_VALUE = "10.0";
    private static final Date UPDATED_ON = new Date();

    private Admin testAdmin;
    private PlatformSettings testSettings;

    @BeforeEach
    void setUp() {
        testAdmin = Admin.builder()
                .id(ADMIN_ID)
                .build();

        testSettings = PlatformSettings.builder()
                .id(SETTING_ID)
                .type(PlatformSettingType.COMMISSION_PERCENTAGE)
                .value(VALID_VALUE)
                .updatedBy(testAdmin)
                .updatedOn(UPDATED_ON)
                .build();
    }

    @Test
    void getAllSettings_Success() {
        List<PlatformSettings> expectedSettings = List.of(testSettings);
        when(platformSettingsRepository.findAll()).thenReturn(expectedSettings);

        List<PlatformSettings> actualSettings = platformSettingsService.getAllSettings();

        assertEquals(expectedSettings.size(), actualSettings.size());
        assertEquals(expectedSettings.get(0), actualSettings.get(0));
        verify(platformSettingsRepository).findAll();
    }

    @Test
    void getSettingByType_Success() {
        when(platformSettingsRepository.findByType(PlatformSettingType.COMMISSION_PERCENTAGE))
                .thenReturn(Optional.of(testSettings));

        PlatformSettings actualSetting = platformSettingsService
                .getSettingByType(PlatformSettingType.COMMISSION_PERCENTAGE);

        assertEquals(testSettings, actualSetting);
        verify(platformSettingsRepository).findByType(PlatformSettingType.COMMISSION_PERCENTAGE);
    }

    @Test
    void getSettingByType_NotFound_ThrowsPlatformSettingsNotFoundException() {
        when(platformSettingsRepository.findByType(PlatformSettingType.COMMISSION_PERCENTAGE))
                .thenReturn(Optional.empty());

        PlatformSettingsNotFoundException exception = assertThrows(
                PlatformSettingsNotFoundException.class,
                () -> platformSettingsService.getSettingByType(PlatformSettingType.COMMISSION_PERCENTAGE));

        assertEquals("Platform settings not found for type: " + PlatformSettingType.COMMISSION_PERCENTAGE,
                exception.getMessage());
        verify(platformSettingsRepository).findByType(PlatformSettingType.COMMISSION_PERCENTAGE);
    }

    @Test
    void updateSetting_Success_ExistingSetting() {
        when(adminService.getAdminById(ADMIN_ID)).thenReturn(testAdmin);
        when(platformSettingsRepository.findByType(PlatformSettingType.COMMISSION_PERCENTAGE))
                .thenReturn(Optional.of(testSettings));
        when(platformSettingsRepository.save(testSettings)).thenReturn(testSettings);

        PlatformSettings actualSetting = platformSettingsService
                .updateSetting(ADMIN_ID, PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE);

        assertEquals(testSettings.getId(), actualSetting.getId());
        assertEquals(testSettings.getType(), actualSetting.getType());
        assertEquals(testSettings.getValue(), actualSetting.getValue());
        assertEquals(testSettings.getUpdatedBy(), actualSetting.getUpdatedBy());
        verify(validator).validateSettingValue(PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE);
        verify(adminService).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository).findByType(PlatformSettingType.COMMISSION_PERCENTAGE);
        verify(platformSettingsRepository).save(testSettings);
    }

    @Test
    void updateSetting_Success_NewSetting() {
        when(adminService.getAdminById(ADMIN_ID)).thenReturn(testAdmin);
        when(platformSettingsRepository.findByType(PlatformSettingType.COMMISSION_PERCENTAGE))
                .thenReturn(Optional.empty());
        when(platformSettingsRepository.save(argThat(setting ->
                setting.getType() == PlatformSettingType.COMMISSION_PERCENTAGE &&
                setting.getValue().equals(VALID_VALUE) &&
                setting.getUpdatedBy().equals(testAdmin))))
                .thenReturn(testSettings);

        PlatformSettings actualSetting = platformSettingsService
                .updateSetting(ADMIN_ID, PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE);

        assertEquals(testSettings.getId(), actualSetting.getId());
        assertEquals(testSettings.getType(), actualSetting.getType());
        assertEquals(testSettings.getValue(), actualSetting.getValue());
        assertEquals(testSettings.getUpdatedBy(), actualSetting.getUpdatedBy());
        verify(validator).validateSettingValue(PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE);
        verify(adminService).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository).findByType(PlatformSettingType.COMMISSION_PERCENTAGE);
        verify(platformSettingsRepository).save(argThat(setting ->
                setting.getType() == PlatformSettingType.COMMISSION_PERCENTAGE &&
                setting.getValue().equals(VALID_VALUE) &&
                setting.getUpdatedBy().equals(testAdmin)));
    }

    @Test
    void updateSetting_NullAdminId_ThrowsInvalidPlatformSettingsException() {
        InvalidPlatformSettingsException exception = assertThrows(
                InvalidPlatformSettingsException.class,
                () -> platformSettingsService.updateSetting(null, PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE));

        assertEquals("Admin ID cannot be null", exception.getMessage());
        verify(adminService, never()).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository, never()).save(testSettings);
    }

    @Test
    void updateSetting_NullType_ThrowsInvalidPlatformSettingsException() {
        InvalidPlatformSettingsException exception = assertThrows(
                InvalidPlatformSettingsException.class,
                () -> platformSettingsService.updateSetting(ADMIN_ID, null, VALID_VALUE));

        assertEquals("Setting type cannot be null", exception.getMessage());
        verify(adminService, never()).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository, never()).save(testSettings);
    }

    @Test
    void updateSetting_NullValue_ThrowsInvalidPlatformSettingsException() {
        InvalidPlatformSettingsException exception = assertThrows(
                InvalidPlatformSettingsException.class,
                () -> platformSettingsService.updateSetting(ADMIN_ID, PlatformSettingType.COMMISSION_PERCENTAGE, null));

        assertEquals("Setting value cannot be null", exception.getMessage());
        verify(adminService, never()).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository, never()).save(testSettings);
    }

    @Test
    void updateSetting_AdminNotFound_ThrowsAdminNotFoundException() {
        when(adminService.getAdminById(ADMIN_ID))
                .thenThrow(new AdminNotFoundException("Admin not found with id: " + ADMIN_ID));

        AdminNotFoundException exception = assertThrows(
                AdminNotFoundException.class,
                () -> platformSettingsService.updateSetting(ADMIN_ID, PlatformSettingType.COMMISSION_PERCENTAGE, VALID_VALUE));

        assertEquals("Admin not found with id: " + ADMIN_ID, exception.getMessage());
        verify(adminService).getAdminById(ADMIN_ID);
        verify(platformSettingsRepository, never()).save(testSettings);
    }
}