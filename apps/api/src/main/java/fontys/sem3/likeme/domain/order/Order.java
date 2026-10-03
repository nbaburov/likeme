package fontys.sem3.likeme.domain.order;

import java.util.Date;

import fontys.sem3.likeme.domain.invoice.Invoice;
import fontys.sem3.likeme.domain.offer.Offer;
import fontys.sem3.likeme.domain.user.client.Client;
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
public class Order {
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;
    @EqualsAndHashCode.Include
    private Offer offer;
    @EqualsAndHashCode.Include
    private OrderDetails details;
    @EqualsAndHashCode.Include
    private Invoice invoice;
    @EqualsAndHashCode.Include
    private Client orderedBy;
    @EqualsAndHashCode.Include
    private Influencer updatedBy;
    @Builder.Default
    private Date createdOn = new Date();
    @Builder.Default
    private Date updatedOn = new Date();
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

}
