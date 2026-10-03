package fontys.sem3.likeme.domain.user.influencer;

import fontys.sem3.likeme.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Influencer extends User {
    @EqualsAndHashCode.Include
    private InfluencerApplication application;
    @EqualsAndHashCode.Include
    private Boolean isInstagramConnected;
    @EqualsAndHashCode.Include
    private String instagramAccessToken;
    @EqualsAndHashCode.Include
    private InfluencerStatus status;
}
