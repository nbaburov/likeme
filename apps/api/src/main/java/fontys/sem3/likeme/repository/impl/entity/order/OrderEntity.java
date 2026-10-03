package fontys.sem3.likeme.repository.impl.entity.order;

import fontys.sem3.likeme.repository.impl.entity.invoice.InvoiceEntity;
import fontys.sem3.likeme.repository.impl.entity.offer.OfferEntity;
import fontys.sem3.likeme.repository.impl.entity.user.client.ClientEntity;
import fontys.sem3.likeme.repository.impl.entity.user.influencer.InfluencerEntity;
import fontys.sem3.likeme.domain.order.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private OfferEntity offer;

    @Embedded
    private OrderDetailsEntity details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id",  nullable = false)
    private InvoiceEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordered_by_id", nullable = false)
    private ClientEntity orderedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_id")
    private InfluencerEntity updatedBy;

    @Column(name = "created_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
}
