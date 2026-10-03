package fontys.sem3.likeme.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.platform.PlatformSettingsConverter;
import fontys.sem3.likeme.business.interfaces.platform.PlatformSettingsService;
import fontys.sem3.likeme.controller.dto.platform.PlatformSettingsResponse;
import fontys.sem3.likeme.controller.dto.platform.UpdateSettingsRequest;
import fontys.sem3.likeme.domain.platform.PlatformSettingType;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/platform/settings")
@RequiredArgsConstructor
@RolesAllowed("ADMIN")
@Tag(name = "Platform Settings")
public class PlatformSettingsController extends BaseController {
        private final PlatformSettingsService platformSettingsService;

        @Operation(summary = "Get all platform settings")
        @GetMapping
        @RolesAllowed("ADMIN")
        public ResponseEntity<List<PlatformSettingsResponse>> getAllSettings() {
                return ResponseEntity.ok(
                                platformSettingsService.getAllSettings().stream()
                                                .map(PlatformSettingsConverter::mapToResponse)
                                                .toList());
        }

        @Operation(summary = "Get setting by type")
        @GetMapping("/{type}")
        @RolesAllowed("ADMIN")
        public ResponseEntity<PlatformSettingsResponse> getSettingByType(
                        @PathVariable PlatformSettingType type) {
                return ResponseEntity.ok(
                                PlatformSettingsConverter.mapToResponse(
                                                platformSettingsService.getSettingByType(type)));
        }

        @Operation(summary = "Update platform settings")
        @PutMapping
        @RolesAllowed("ADMIN")
        public ResponseEntity<List<PlatformSettingsResponse>> updateSettings(
                        @Valid @RequestBody UpdateSettingsRequest request) {

                Long adminId = getCurrentUserId();
                validateOwnership(adminId);

                List<PlatformSettingsResponse> updatedSettings = request.getSettings().stream()
                                .map(setting -> platformSettingsService.updateSetting(
                                                adminId,
                                                setting.getType(),
                                                setting.getValue()))
                                .map(PlatformSettingsConverter::mapToResponse)
                                .toList();

                return ResponseEntity.ok(updatedSettings);
        }

        @Operation(summary = "Update single setting")
        @PutMapping("/{type}")
        @RolesAllowed("ADMIN")
        public ResponseEntity<PlatformSettingsResponse> updateSetting(
                        @PathVariable PlatformSettingType type,
                        @Valid @RequestBody String value) {

                Long adminId = getCurrentUserId();
                validateOwnership(adminId);

                return ResponseEntity.ok(
                                PlatformSettingsConverter.mapToResponse(
                                                platformSettingsService.updateSetting(
                                                                adminId,
                                                                type,
                                                                value)));
        }

}