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

import fontys.sem3.likeme.business.converter.offer.OfferConverter;
import fontys.sem3.likeme.business.interfaces.offer.OfferService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.business.validator.offer.OfferValidator;
import fontys.sem3.likeme.controller.dto.offer.CreateOfferRequest;
import fontys.sem3.likeme.controller.dto.offer.OfferResponse;
import fontys.sem3.likeme.controller.dto.offer.UpdateOfferRequest;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.offer.OfferType;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
@Tag(name = "Offer Controller", description = "Endpoints for offer management")
public class OfferController extends BaseController {
    private final OfferService offerService;
    private final OfferValidator offerValidator;
    private final InfluencerService influencerService;

    @Operation(summary = "Create offer", description = "Create a new offer")
    @PostMapping
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<OfferResponse> createOffer(
            @Valid @RequestBody CreateOfferRequest request) {
        Influencer creator = influencerService.getInfluencerById(request.getCreatedById());

        Offer offer = OfferConverter.requestToDomain(request, creator);
        return new ResponseEntity<>(OfferConverter.toResponse(offerService.createOffer(offer)), HttpStatus.CREATED);
    }

    @Operation(summary = "Get offer by ID", description = "Retrieve offer details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OfferResponse> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(OfferConverter.toResponse(offerService.getOffer(id)));
    }

    @Operation(summary = "Get offers by type", description = "Retrieve offers by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<OfferResponse>> getOffersByType(@PathVariable OfferType type) {
        List<OfferResponse> responses = offerService.getOffersByType(type).stream()
                .map(OfferConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get offers by influencer", description = "Retrieve offers by influencer ID")
    @GetMapping("/influencer/{influencerId}")
    public ResponseEntity<List<OfferResponse>> getOffersByInfluencer(@PathVariable Long influencerId) {
        List<OfferResponse> responses = offerService.getOffersByInfluencer(influencerId).stream()
                .map(OfferConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all offers", description = "Retrieve all offers")
    @GetMapping
    public ResponseEntity<List<OfferResponse>> getAllOffers() {
        List<OfferResponse> responses = offerService.getAllOffers().stream()
                .map(OfferConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Update offer", description = "Update offer details")
    @PutMapping("/{id}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<OfferResponse> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOfferRequest request) {
        Offer existingOffer = offerService.getOffer(id);
        validateOwnership(existingOffer.getCreatedBy().getId());
        Influencer updater = influencerService.getInfluencerById(existingOffer.getCreatedBy().getId());
        offerValidator.validateUpdate(request, existingOffer, updater);

        Offer offer = OfferConverter.requestToDomain(request, id, existingOffer, updater);
        return ResponseEntity.ok(OfferConverter.toResponse(offerService.updateOffer(offer)));
    }

    @Operation(summary = "Delete offer", description = "Delete an offer")
    @DeleteMapping("/{id}")
    @RolesAllowed({ "ADMIN", "INFLUENCER" })
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        Offer existingOffer = offerService.getOffer(id);
        validateOwnership(existingOffer.getCreatedBy().getId());
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}