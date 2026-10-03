package fontys.sem3.likeme.business.converter.user.billing;

import fontys.sem3.likeme.business.exception.InvalidRequest;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationConversionException;
import fontys.sem3.likeme.business.exception.user.influencer.InvalidInfluencerApplicationException;
import fontys.sem3.likeme.controller.dto.billing.BillingDetailsResponse;
import fontys.sem3.likeme.controller.dto.billing.CreateBillingDetailsRequest;
import fontys.sem3.likeme.controller.dto.billing.UpdateBillingDetailsRequest;
import fontys.sem3.likeme.domain.user.BillingDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingDetailsConverter {

    public static BillingDetails toBillingDetails(CreateBillingDetailsRequest request) {
        if (request == null) {
            throw new InvalidRequest("Billing details request cannot be null");
        }

        try {
            return BillingDetails.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .country(request.getCountry())
                    .streetAddress(request.getStreetAddress())
                    .city(request.getCity())
                    .state(request.getState())
                    .zipCode(request.getZipCode())
                    .build();
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationConversionException(
                    "Failed to convert billing details request to billing details: "
                            + e.getMessage(),
                    e);
        }
    }

    public static BillingDetails toBillingDetails(UpdateBillingDetailsRequest request,
            BillingDetails existingBillingDetails) {

        if (request == null) {
            return existingBillingDetails;
        }
        return BillingDetails.builder()
                .firstName(request.getFirstName() != null ? request.getFirstName()
                        : existingBillingDetails.getFirstName())
                .lastName(request.getLastName() != null ? request.getLastName()
                        : existingBillingDetails.getLastName())
                .country(request.getCountry() != null ? request.getCountry()
                        : existingBillingDetails.getCountry())
                .streetAddress(request.getStreetAddress() != null ? request.getStreetAddress()
                        : existingBillingDetails.getStreetAddress())
                .city(request.getCity() != null ? request.getCity() : existingBillingDetails.getCity())
                .state(request.getState() != null ? request.getState()
                        : existingBillingDetails.getState())
                .zipCode(request.getZipCode() != null ? request.getZipCode()
                        : existingBillingDetails.getZipCode())
                .build();
    }

    public static BillingDetailsResponse toBillingDetailsResponse(BillingDetails billingDetails) {
        if (billingDetails == null) {
            throw new InvalidInfluencerApplicationException("Billing details cannot be null");
        }

        try {
            return BillingDetailsResponse.builder()
                    .firstName(billingDetails.getFirstName())
                    .lastName(billingDetails.getLastName())
                    .country(billingDetails.getCountry())
                    .streetAddress(billingDetails.getStreetAddress())
                    .city(billingDetails.getCity())
                    .state(billingDetails.getState())
                    .zipCode(billingDetails.getZipCode())
                    .build();
        } catch (Exception e) {
            throw new InvalidInfluencerApplicationConversionException(
                    "Failed to convert billing details to response: " + e.getMessage(), e);
        }
    }
}
