package fontys.sem3.likeme.domain.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private AccessToken accessToken;
    private RefreshToken refreshToken;
} 