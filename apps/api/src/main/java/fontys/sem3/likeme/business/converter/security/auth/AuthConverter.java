package fontys.sem3.likeme.business.converter.security.auth;

import fontys.sem3.likeme.controller.dto.auth.AuthResponse;
import fontys.sem3.likeme.domain.security.jwt.TokenResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConverter {

    public static AuthResponse mapToResponse(TokenResponse tokens) {
        if (tokens == null) return null;

        return AuthResponse.builder()
                .accessToken(tokens.getAccessToken().getToken())
                .refreshToken(tokens.getRefreshToken().getToken())
                .build();
    }
}
