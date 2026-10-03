package fontys.sem3.likeme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.user.influencer.InfluencerConverter;
import fontys.sem3.likeme.business.converter.user.influencer.application.InfluencerApplicationConverter;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerApplicationService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerApplicationValidator;
import fontys.sem3.likeme.business.validator.user.influencer.InfluencerValidator;
import fontys.sem3.likeme.controller.dto.influencer.CompleteSetupRequest;
import fontys.sem3.likeme.controller.dto.influencer.ConnectInstagramRequest;
import fontys.sem3.likeme.controller.dto.influencer.CreateInfluencerRequest;
import fontys.sem3.likeme.controller.dto.influencer.InfluencerResponse;
import fontys.sem3.likeme.controller.dto.influencer.UpdateInfluencerRequest;
import fontys.sem3.likeme.controller.dto.influencer.application.CreateInfluencerApplicationRequest;
import fontys.sem3.likeme.controller.dto.influencer.application.InfluencerApplicationResponse;
import fontys.sem3.likeme.controller.dto.influencer.application.UpdateInfluencerApplicationRequest;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/influencers")
@RequiredArgsConstructor
@Tag(name = "Influencer Controller", description = "Endpoints for influencer management")
public class InfluencerController extends BaseController {
    private final InfluencerApplicationService influencerApplicationService;
    private final InfluencerService influencerService;
    private final InfluencerApplicationValidator influencerApplicationValidator;
    private final InfluencerValidator influencerValidator;

    @Operation(summary = "Get all influencers", description = "Retrieve all influencers")
    @GetMapping
    public ResponseEntity<List<InfluencerResponse>> getAllInfluencers() {
        List<InfluencerResponse> responses = influencerService.getAllInfluencers()
                .stream()
                .map(InfluencerConverter::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get influencer by ID", description = "Retrieve influencer details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<InfluencerResponse> getInfluencerById(@PathVariable Long id) {
        Influencer influencer = influencerService.getInfluencerById(id);
        return ResponseEntity.ok(InfluencerConverter.mapToResponse(influencer));
    }

    @Operation(summary = "Create influencer", description = "Create a new influencer")
    @RolesAllowed({ "ADMIN" })
    @PostMapping
    public ResponseEntity<InfluencerResponse> createInfluencer(
            @Valid @RequestBody CreateInfluencerRequest request) {
        Influencer domain = InfluencerConverter.toInfluencer(request);
        Influencer created = influencerService.createInfluencer(domain);
        return ResponseEntity.ok(InfluencerConverter.mapToResponse(created));
    }

    @Operation(summary = "Update influencer", description = "Update influencer details")
    @RolesAllowed({ "INFLUENCER", "ADMIN" })
    @PutMapping("/{id}")
    public ResponseEntity<InfluencerResponse> updateInfluencer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInfluencerRequest request) {
        validateOwnership(id);
        Influencer existingInfluencer = influencerService.getInfluencerById(id);

        influencerValidator.validateUpdate(request, existingInfluencer);

        Influencer influencer = InfluencerConverter.toInfluencer(request, id, existingInfluencer);
        Influencer updated = influencerService.updateInfluencer(influencer);

        return ResponseEntity.ok(InfluencerConverter.mapToResponse(updated));
    }

    @Operation(summary = "Delete influencer", description = "Delete an influencer")
    @DeleteMapping("/{id}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<Void> deleteInfluencer(@PathVariable Long id) {
        validateOwnership(id);
        influencerService.deleteInfluencer(id);
        return ResponseEntity.noContent().build();
    }

    // Setup

    @Operation(summary = "Complete account setup", description = "First step of influencer setup - setting up account password")
    @PostMapping("/setup/account")
    public ResponseEntity<InfluencerResponse> completeAccountSetup(@Valid @RequestBody CompleteSetupRequest request) {
        Influencer influencer = influencerService.completeInfluencerSetup(request.getToken(), request.getPassword());
        return ResponseEntity.ok(InfluencerConverter.mapToResponse(influencer));
    }

    @Operation(summary = "Connect Instagram account", description = "Second step of influencer setup - connecting Instagram account")
    @PostMapping("/setup/instagram/{id}")
    @RolesAllowed({ "INFLUENCER", "ADMIN" })
    public ResponseEntity<InfluencerResponse> connectInstagram(
            @PathVariable Long id,
            @Valid @RequestBody ConnectInstagramRequest request) {
        validateOwnership(id);
        Influencer updatedInfluencer = influencerService.connectInstagram(id, request.getInstagramAccessToken());
        return ResponseEntity.ok(InfluencerConverter.mapToResponse(updatedInfluencer));
    }

    // Applications

    @Operation(summary = "Get all applications", description = "Retrieve all influencer applications")
    @GetMapping("/applications")
    @RolesAllowed({ "INFLUENCER", "ADMIN" })
    public ResponseEntity<List<InfluencerApplicationResponse>> getInfluencerApplications() {
        List<InfluencerApplicationResponse> responses = influencerApplicationService.getInfluencerApplications()
                .stream()
                .map(InfluencerApplicationConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get application by ID", description = "Retrieve application details by ID")
    @RolesAllowed({ "INFLUENCER", "ADMIN" })
    @GetMapping("/applications/{id}")
    public ResponseEntity<InfluencerApplicationResponse> getInfluencerApplicationById(@PathVariable Long id) {
        InfluencerApplication application = influencerApplicationService.getInfluencerApplicationById(id);
        return ResponseEntity.ok(InfluencerApplicationConverter.toResponse(application));
    }

    @Operation(summary = "Create application", description = "Create a new influencer application")
    @PostMapping("/applications")
    public ResponseEntity<InfluencerApplicationResponse> createInfluencerApplication(
            @Valid @RequestBody CreateInfluencerApplicationRequest request) {
        InfluencerApplication application = InfluencerApplicationConverter.toInfluencerApplication(request);
        InfluencerApplication created = influencerApplicationService.createInfluencerApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InfluencerApplicationConverter.toResponse(created));
    }

    @Operation(summary = "Update application", description = "Update application details")
    @RolesAllowed({"ADMIN" })
    @PutMapping("/applications/{id}")
    public ResponseEntity<InfluencerApplicationResponse> updateInfluencerApplication(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInfluencerApplicationRequest request) {
        InfluencerApplication existingApplication = influencerApplicationService.getInfluencerApplicationById(id);

        influencerApplicationValidator.validateUpdate(request, existingApplication);

        InfluencerApplication application = InfluencerApplicationConverter.toInfluencerApplication(request, id,
                existingApplication);
        InfluencerApplication updated = influencerApplicationService.updateInfluencerApplication(application);
        return ResponseEntity.ok(InfluencerApplicationConverter.toResponse(updated));
    }

    @Operation(summary = "Delete application", description = "Delete an application")
    @RolesAllowed({ "ADMIN" })
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteInfluencerApplication(@PathVariable Long id) {
        influencerApplicationService.deleteInfluencerApplication(id);
        return ResponseEntity.noContent().build();
    }
}