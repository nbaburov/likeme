package fontys.sem3.likeme.domain.user.client;

import fontys.sem3.likeme.domain.user.BillingDetails;
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
public class Client extends User {
    @EqualsAndHashCode.Include
    private String instagramHandle;
    @EqualsAndHashCode.Include
    private Boolean isInstagramConnected;
    @EqualsAndHashCode.Include
    private String instagramAccessToken;
    @EqualsAndHashCode.Include
    private BillingDetails billingDetails;
}
