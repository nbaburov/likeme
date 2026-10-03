package fontys.sem3.likeme.controller.dto.influencer.application;

import fontys.sem3.likeme.controller.dto.billing.CreateBillingDetailsRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInfluencerApplicationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?\\d{10,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "About section is required")
    @Size(max = 500, message = "About section must not exceed 500 characters")
    private String about;

    @NotBlank(message = "Instagram handle is required")
    @Pattern(regexp = "^[a-zA-Z0-9._]{1,30}$", message = "Invalid Instagram handle format")
    private String instagramHandle;

    @NotBlank(message = "Profile photo is required")
    private String profilePhotoPath;

    @NotBlank(message = "Cover photo is required")
    private String coverPhotoPath;

    @Valid
    private CreateBillingDetailsRequest billingDetails;
}