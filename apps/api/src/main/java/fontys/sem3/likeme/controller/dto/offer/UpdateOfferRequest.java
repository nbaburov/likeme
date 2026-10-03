package fontys.sem3.likeme.controller.dto.offer;

import fontys.sem3.likeme.domain.offer.OfferType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOfferRequest {
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String coverPhotoPath;

    private OfferType type;
    private Boolean isActive;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;
}
