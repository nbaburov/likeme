package fontys.sem3.likeme.controller.dto.offer;

import java.util.Date;

import fontys.sem3.likeme.domain.offer.OfferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferResponse {
    private Long id;
    private String title;
    private String description;
    private String coverPhotoPath;
    private OfferType type;
    private Boolean isActive;
    private Date createdOn;
    private Date updatedOn;
    private Long createdById;
    private Long updatedById;
    private Double price;
}