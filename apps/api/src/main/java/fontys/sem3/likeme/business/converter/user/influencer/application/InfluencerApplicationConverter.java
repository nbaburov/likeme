package fontys.sem3.likeme.business.converter.user.influencer.application;

import java.util.Date;

import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationConversionException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationException;
import static fontys.sem3.likeme.business.converter.user.billing.BillingDetailsConverter.toBillingDetails;
import static fontys.sem3.likeme.business.converter.user.billing.BillingDetailsConverter.toBillingDetailsResponse;
import fontys.sem3.likeme.controller.dto.influencer.application.CreateInfluencerApplicationRequest;
import fontys.sem3.likeme.controller.dto.influencer.application.InfluencerApplicationResponse;
import fontys.sem3.likeme.controller.dto.influencer.application.UpdateInfluencerApplicationRequest;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InfluencerApplicationConverter {

        public static InfluencerApplication toInfluencerApplication(CreateInfluencerApplicationRequest request) {
                if (request == null) {
                        throw new InvalidRequest("Influencer application request cannot be null");
                }

                try {
                        return InfluencerApplication.builder()
                                        .username(request.getUsername())
                                        .email(request.getEmail())
                                        .phoneNumber(request.getPhoneNumber())
                                        .about(request.getAbout())
                                        .instagramHandle(request.getInstagramHandle())
                                        .billingDetails(toBillingDetails(request.getBillingDetails()))
                                        .profilePhotoPath(request.getProfilePhotoPath())
                                        .coverPhotoPath(request.getCoverPhotoPath())
                                        .isApproved(false)
                                        .createdOn(new Date())
                                        .updatedOn(new Date())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidInfluencerApplicationConversionException(
                                        "Failed to create influencer application: " + e.getMessage(), e);
                }
        }

        public static InfluencerApplication toInfluencerApplication(
                        UpdateInfluencerApplicationRequest request,
                        Long id,
                        InfluencerApplication existingApplication) {

                if (request == null || existingApplication == null) {
                        throw new InvalidRequest("Update request and existing application cannot be null");
                }

                try {
                        return InfluencerApplication.builder()
                                        .id(id)
                                        .username(request.getUsername() != null ? request.getUsername()
                                                        : existingApplication.getUsername())
                                        .email(request.getEmail() != null ? request.getEmail()
                                                        : existingApplication.getEmail())
                                        .phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber()
                                                        : existingApplication.getPhoneNumber())
                                        .about(request.getAbout() != null ? request.getAbout()
                                                        : existingApplication.getAbout())
                                        .instagramHandle(request.getInstagramHandle() != null
                                                        ? request.getInstagramHandle()
                                                        : existingApplication.getInstagramHandle())
                                        .billingDetails(toBillingDetails(request.getBillingDetails(),
                                                        existingApplication.getBillingDetails()))
                                        .profilePhotoPath(request.getProfilePhotoPath() != null
                                                        ? request.getProfilePhotoPath()
                                                        : existingApplication.getProfilePhotoPath())
                                        .coverPhotoPath(request.getCoverPhotoPath() != null
                                                        ? request.getCoverPhotoPath()
                                                        : existingApplication.getCoverPhotoPath())
                                        .isApproved(request.getIsApproved() != null ? request.getIsApproved()
                                                        : existingApplication.getIsApproved())
                                        .createdOn(existingApplication.getCreatedOn())
                                        .updatedOn(new Date())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidInfluencerApplicationConversionException(
                                        "Failed to update influencer application: " + e.getMessage(), e);
                }
        }

        public static InfluencerApplicationResponse toResponse(InfluencerApplication application) {
                if (application == null) {
                        throw new InvalidInfluencerApplicationException("Application cannot be null");
                }

                try {
                        return InfluencerApplicationResponse.builder()
                                        .id(application.getId())
                                        .username(application.getUsername())
                                        .email(application.getEmail())
                                        .phoneNumber(application.getPhoneNumber())
                                        .about(application.getAbout())
                                        .instagramHandle(application.getInstagramHandle())
                                        .isApproved(application.getIsApproved())
                                        .billingDetails(toBillingDetailsResponse(application.getBillingDetails()))
                                        .profilePhotoPath(application.getProfilePhotoPath())
                                        .coverPhotoPath(application.getCoverPhotoPath())
                                        .createdOn(application.getCreatedOn())
                                        .updatedOn(application.getUpdatedOn())
                                        .build();
                } catch (Exception e) {
                        throw new InvalidInfluencerApplicationConversionException(
                                        "Failed to convert application to response: " + e.getMessage(), e);
                }
        }

}
