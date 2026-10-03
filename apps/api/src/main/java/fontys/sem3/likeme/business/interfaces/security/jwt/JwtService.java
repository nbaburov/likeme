package fontys.sem3.likeme.business.interfaces.security.jwt;

import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.user.User;

public interface JwtService {
    AccessToken generateToken(User user);
    RefreshToken generateRefreshToken(User user);
    boolean isTokenValid(String token);
    AccessToken decodeToken(String token);
    RefreshToken decodeRefreshToken(String token);
}
