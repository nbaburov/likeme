package fontys.sem3.likeme.domain.offer;

import java.util.Date;

import fontys.sem3.likeme.domain.user.influencer.Influencer;
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
public class Offer {
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private String title;
    @EqualsAndHashCode.Include
    private String description;
    @EqualsAndHashCode.Include
    private String coverPhotoPath;
    @EqualsAndHashCode.Include
    private OfferType type;
    @EqualsAndHashCode.Include
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Date createdOn = new Date();
    @Builder.Default
    private Date updatedOn = new Date();
    @EqualsAndHashCode.Include
    private Influencer createdBy;
    @EqualsAndHashCode.Include
    private Influencer updatedBy;
    @EqualsAndHashCode.Include
    private Double price;
}
