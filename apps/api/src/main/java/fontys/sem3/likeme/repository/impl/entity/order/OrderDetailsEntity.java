package fontys.sem3.likeme.repository.impl.entity.order;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderDetailsEntity {
    @Column(name = "post_id")
    private String postId;

    @Column(name = "comment")
    private String comment;
}
