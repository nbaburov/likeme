package fontys.sem3.likeme.controller.dto.client;

import java.util.Date;

import fontys.sem3.likeme.controller.dto.billing.BillingDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String username;
    private String email;
    private String profilePhotoPath;
    private String instagramHandle;
    private Boolean isInstagramConnected;
    private String instagramAccessToken;
    private BillingDetailsResponse billingDetails;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
    private Date lastLoginOn;
} 