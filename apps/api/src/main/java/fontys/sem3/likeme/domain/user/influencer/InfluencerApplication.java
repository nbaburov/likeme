package fontys.sem3.likeme.domain.user.influencer;

import java.util.Date;

import fontys.sem3.likeme.domain.user.BillingDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InfluencerApplication {
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String username;
    @EqualsAndHashCode.Include
    private String email;
    @EqualsAndHashCode.Include
    private String phoneNumber;
    @EqualsAndHashCode.Include
    private String about;
    @EqualsAndHashCode.Include
    private String instagramHandle;
    @Builder.Default
    @EqualsAndHashCode.Include
    private Boolean isApproved = false;
    private BillingDetails billingDetails;
    @EqualsAndHashCode.Include
    private String profilePhotoPath;
    @EqualsAndHashCode.Include
    private String coverPhotoPath;
    @Builder.Default
    private Date createdOn = new Date();
    @Builder.Default
    private Date updatedOn = new Date();
}
