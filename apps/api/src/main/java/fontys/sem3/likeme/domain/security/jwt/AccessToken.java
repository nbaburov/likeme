package fontys.sem3.likeme.domain.security.jwt;

import fontys.sem3.likeme.domain.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {
    private String token;
    private String subject;
    private Long userId;
    private Role role;
}
