package fontys.sem3.likeme.controller.dto.influencer.application;

import fontys.sem3.likeme.controller.dto.billing.UpdateBillingDetailsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInfluencerApplicationRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?\\d{10,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 500, message = "About section must not exceed 500 characters")
    private String about;

    @Pattern(regexp = "^[a-zA-Z0-9._]{1,30}$", message = "Invalid Instagram handle format")
    private String instagramHandle;

    private String profilePhotoPath;

    private String coverPhotoPath;

    private Boolean isApproved;

    @Valid
    private UpdateBillingDetailsRequest billingDetails;
}
