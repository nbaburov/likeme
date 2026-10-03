package fontys.sem3.likeme.repository.impl.mapper;

import fontys.sem3.likeme.domain.order.OrderDetails;
import fontys.sem3.likeme.repository.impl.entity.order.OrderDetailsEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailsMapper {
    public static OrderDetailsEntity mapToEntity(OrderDetails details) {
        if (details == null) return null;

        return OrderDetailsEntity.builder()
                .postId(details.getPostId())
                .comment(details.getComment())
                .build();
    }

    public static OrderDetails mapToDomain(OrderDetailsEntity entity) {
        if (entity == null) return null;

        return OrderDetails.builder()
                .postId(entity.getPostId())
                .comment(entity.getComment())
                .build();
    }
} 