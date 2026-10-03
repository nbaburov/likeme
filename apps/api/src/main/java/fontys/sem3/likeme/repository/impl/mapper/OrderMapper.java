package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.order.Order;
import fontys.sem3.likeme.repository.impl.entity.order.OrderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {
    public static OrderEntity mapToEntity(Order order) {
        if (order == null)
            return null;

        return OrderEntity.builder()
                .id(order.getId())
                .offer(OfferMapper.mapToEntity(order.getOffer()))
                .details(OrderDetailsMapper.mapToEntity(order.getDetails()))
                .invoice(InvoiceMapper.mapToEntity(order.getInvoice()))
                .orderedBy(ClientMapper.mapToEntity(order.getOrderedBy()))
                .updatedBy(InfluencerMapper.mapToEntity(order.getUpdatedBy()))
                .createdOn(order.getCreatedOn())
                .updatedOn(order.getUpdatedOn())
                .status(order.getStatus())
                .build();
    }

    public static Order mapToDomain(OrderEntity entity) {
        if (entity == null)
            return null;

        return Order.builder()
                .id(entity.getId())
                .offer(OfferMapper.mapToDomain(entity.getOffer()))
                .details(OrderDetailsMapper.mapToDomain(entity.getDetails()))
                .invoice(InvoiceMapper.mapToDomain(entity.getInvoice()))
                .orderedBy(ClientMapper.mapToDomain(entity.getOrderedBy()))
                .updatedBy(InfluencerMapper.mapToDomain(entity.getUpdatedBy()))
                .createdOn(entity.getCreatedOn())
                .updatedOn(entity.getUpdatedOn())
                .status(entity.getStatus())
                .build();
    }
}