package fontys.sem3.likeme.business.interfaces.security.auth;

import fontys.sem3.likeme.domain.security.jwt.TokenResponse;

public interface AuthService {
    TokenResponse authenticate(String username, String password);
    TokenResponse refreshToken(String refreshToken);
}
