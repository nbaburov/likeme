package fontys.sem3.likeme.controller.dto.offer;

import fontys.sem3.likeme.domain.offer.OfferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfferRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Cover photo is required")
    private String coverPhotoPath;

    @NotNull(message = "Offer type is required")
    private OfferType type;

    @NotNull(message = "Created by is required")
    private Long createdById;

    @NotNull(message = "Price is required")
    private Double price;
} 