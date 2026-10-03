package fontys.sem3.likeme.business.converter.user.influencer;

import java.util.Date;

import fontys.sem3.likeme.business.converter.user.influencer.application.InfluencerApplicationConverter;
import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerConversionException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerDataException;
import fontys.sem3.likeme.controller.dto.influencer.CreateInfluencerRequest;
import fontys.sem3.likeme.controller.dto.influencer.InfluencerResponse;
import fontys.sem3.likeme.controller.dto.influencer.UpdateInfluencerRequest;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InfluencerConverter {
    public static Influencer toInfluencer(CreateInfluencerRequest request) {
        if (request == null) {
            throw new InvalidRequest("CreateInfluencerRequest cannot be null");
        }

        try {
            return Influencer.builder()
                    .application(InfluencerApplicationConverter.toInfluencerApplication(request.getApplication()))
                    .password(request.getPassword())
                    .status(request.getStatus())
                    .isInstagramConnected(request.getIsInstagramConnected())
                    .isActive(true)
                    .build();
        } catch (Exception e) {
            throw new InfluencerConversionException("Failed to convert CreateInfluencerRequest to Influencer", e);
        }
    }

    public static Influencer toInfluencer(UpdateInfluencerRequest request, Long id, Influencer existingInfluencer) {
        if (request == null || existingInfluencer == null || id == null) {
            throw new InvalidRequest("UpdateInfluencerRequest cannot be null");
        }

        try {
            return Influencer.builder()
                    .id(id)
                    .application(request.getApplication() != null
                            ? InfluencerApplicationConverter.toInfluencerApplication(request.getApplication(), id,
                                    existingInfluencer.getApplication())
                            : existingInfluencer.getApplication())
                    .password(request.getPassword() != null ? request.getPassword() : existingInfluencer.getPassword())
                    .salt(existingInfluencer.getSalt())
                    .isInstagramConnected(request.getIsInstagramConnected() != null ? request.getIsInstagramConnected()
                            : existingInfluencer.getIsInstagramConnected())
                    .instagramAccessToken(request.getInstagramAccessToken() != null ? request.getInstagramAccessToken()
                            : existingInfluencer.getInstagramAccessToken())
                    .status(request.getStatus() != null ? request.getStatus() : existingInfluencer.getStatus())
                    .isActive(request.getIsActive() != null ? request.getIsActive() : existingInfluencer.getIsActive())
                    .createdOn(existingInfluencer.getCreatedOn())
                    .updatedOn(new Date())
                    .build();
        } catch (Exception e) {
            throw new InfluencerConversionException("Failed to convert UpdateInfluencerRequest to Influencer", e);
        }
    }

    public static InfluencerResponse mapToResponse(Influencer influencer) {
        if (influencer == null) {
            throw new InvalidInfluencerDataException("Influencer cannot be null");
        }

        try {
            return InfluencerResponse.builder()
                    .id(influencer.getId())
                    .application(InfluencerApplicationConverter.toResponse(influencer.getApplication()))
                    .isInstagramConnected(influencer.getIsInstagramConnected())
                    .instagramAccessToken(influencer.getInstagramAccessToken())
                    .status(influencer.getStatus())
                    .isActive(influencer.getIsActive())
                    .createdOn(influencer.getCreatedOn())
                    .updatedOn(influencer.getUpdatedOn())
                    .build();
        } catch (Exception e) {
            throw new InfluencerConversionException("Failed to convert Influencer to InfluencerResponse", e);
        }
    }
}
