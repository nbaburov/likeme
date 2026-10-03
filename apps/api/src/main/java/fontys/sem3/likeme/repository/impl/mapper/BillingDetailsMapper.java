package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.user.BillingDetails;
import fontys.sem3.likeme.repository.impl.entity.user.BillingDetailsEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BillingDetailsMapper {
    public static BillingDetailsEntity mapToEntityBillingDetails(BillingDetails billingDetails) {
        return BillingDetailsEntity.builder()
                .firstName(billingDetails.getFirstName())
                .lastName(billingDetails.getLastName())
                .country(billingDetails.getCountry())
                .streetAddress(billingDetails.getStreetAddress())
                .city(billingDetails.getCity())
                .state(billingDetails.getState())
                .zipCode(billingDetails.getZipCode())
                .build();
    }

    public static BillingDetails mapToDomainBillingDetails(BillingDetailsEntity billingDetailsEntity) {
        return BillingDetails.builder()
                .firstName(billingDetailsEntity.getFirstName())
                .lastName(billingDetailsEntity.getLastName())
                .country(billingDetailsEntity.getCountry())
                .streetAddress(billingDetailsEntity.getStreetAddress())
                .city(billingDetailsEntity.getCity())
                .state(billingDetailsEntity.getState())
                .zipCode(billingDetailsEntity.getZipCode())
                .build();
    }
}
