package fontys.sem3.likeme.repository.impl.entity.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetailsEntity {
    @Column(name = "billing_first_name", nullable = false)
    private String firstName;

    @Column(name = "billing_last_name", nullable = false)
    private String lastName;

    @Column(name = "billing_country", nullable = false)
    private String country;

    @Column(name = "billing_street_address", nullable = false)
    private String streetAddress;

    @Column(name = "billing_city", nullable = false)
    private String city;

    @Column(name = "billing_state", nullable = false)
    private String state;

    @Column(name = "billing_zip_code", nullable = false)
    private String zipCode;
}
