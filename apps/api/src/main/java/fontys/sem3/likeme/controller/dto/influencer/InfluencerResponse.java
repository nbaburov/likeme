package fontys.sem3.likeme.controller.dto.influencer;

import java.util.Date;

import fontys.sem3.likeme.controller.dto.influencer.application.InfluencerApplicationResponse;
import fontys.sem3.likeme.domain.user.influencer.InfluencerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerResponse {
    private Long id;
    private InfluencerApplicationResponse application;
    private Boolean isInstagramConnected;
    private String instagramAccessToken;
    private InfluencerStatus status;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
    private Date lastLoginOn;
}