package fontys.sem3.likeme.controller.dto.billing;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBillingDetailsRequest {
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    private String country;
    private String streetAddress;
    private String city;
    private String state;
    @Pattern(regexp = "^[A-Z0-9]{1,8}$", message = "Invalid zip code format")
    private String zipCode;
}
