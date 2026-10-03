package fontys.sem3.likeme.controller.dto.influencer.application;

import fontys.sem3.likeme.controller.dto.billing.BillingDetailsResponse;
import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerApplicationResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String about;
    private String instagramHandle;
    private String profilePhotoPath;
    private String coverPhotoPath;
    private Boolean isApproved;
    private BillingDetailsResponse billingDetails;
    private Date createdOn;
    private Date updatedOn;
}