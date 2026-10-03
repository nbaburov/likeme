package fontys.sem3.likeme.business.impl.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fontys.sem3.likeme.business.exception.security.auth.ExpiredTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidRefreshTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidTokenException;
import fontys.sem3.likeme.business.exception.security.auth.RefreshTokenException;
import fontys.sem3.likeme.business.exception.security.jwt.JwtServiceException;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.User;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    private static final String CLAIM_USER_ID = "userId";

    @Override
    public AccessToken generateToken(User user) {
        try {
            String username = "";
            Map<String, Object> claims = new HashMap<>();
            claims.put(CLAIM_USER_ID, user.getId());
            claims.put("role", user.getRole().toString());

            if (user instanceof Admin admin) {
                claims.put("permissions", admin.getPermissions());
                username = admin.getUsername();
            } else if (user instanceof Influencer influencer) {
                username = influencer.getApplication().getUsername();
            } else if (user instanceof Client client) {
                username = client.getUsername();
            } else {
                throw new JwtServiceException("Unsupported user type");
            }

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            return AccessToken.builder()
                    .token(token)
                    .role(user.getRole())
                    .userId(user.getId())
                    .subject(username)
                    .build();
        } catch (Exception e) {
            throw new JwtServiceException("Failed to generate JWT token", e);
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid JWT token", e);
        } catch (Exception e) {
            throw new JwtServiceException("Failed to validate JWT token", e);
        }
    }

    @Override
    public AccessToken decodeToken(String token) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token);
            Claims claims = jwt.getBody();

            Long userId = claims.get(CLAIM_USER_ID, Long.class);
            String role = claims.get("role", String.class);

            return AccessToken.builder()
                    .token(token)
                    .subject(claims.getSubject())
                    .userId(userId)
                    .role(Role.valueOf(role))
                    .build();
        } catch (JwtException e) {
            throw new InvalidTokenException("Failed to decode JWT token", e);
        } catch (Exception e) {
            throw new JwtServiceException("Failed to decode JWT token", e);
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw e; // Let it be handled by the calling method
        } catch (JwtException e) {
            throw new InvalidTokenException("Failed to extract claims from JWT token", e);
        } catch (Exception e) {
            throw new InvalidTokenException("Failed to process JWT token", e);
        }
    }

    private Key getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(secretKey.getBytes());
        } catch (Exception e) {
            throw new JwtServiceException("Failed to get signing key for JWT", e);
        }
    }

    @Override
    public RefreshToken generateRefreshToken(User user) {
        try {
            String username = "";
            Map<String, Object> claims = new HashMap<>();
            claims.put(CLAIM_USER_ID, user.getId());
            claims.put("tokenType", "refresh");
            if (user instanceof Admin admin) {
                claims.put("permissions", admin.getPermissions());
                username = admin.getUsername();
            } else if (user instanceof Influencer influencer) {
                username = influencer.getApplication().getUsername();
            } else if (user instanceof Client client) {
                username = client.getUsername();
            } else {
                throw new JwtServiceException("Unsupported user type");
            }

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            return RefreshToken.builder()
                    .token(token)
                    .userId(user.getId())
                    .subject(username)
                    .build();
        } catch (Exception e) {
            throw new JwtServiceException("Failed to generate refresh token", e);
        }
    }

    @Override
    public RefreshToken decodeRefreshToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidRefreshTokenException("Token cannot be null or empty");
        }

        try {
            Claims claims = extractAllClaims(token);

            if (!"refresh".equals(claims.get("tokenType"))) {
                throw new InvalidRefreshTokenException("Invalid token type");
            }

            if (claims.getExpiration().before(new Date())) {
                throw new ExpiredTokenException("Refresh token has expired");
            }

            return RefreshToken.builder()
                    .token(token)
                    .subject(claims.getSubject())
                    .userId(claims.get(CLAIM_USER_ID, Long.class))
                    .build();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Refresh token has expired", e);
        } catch (JwtException e) {
            throw new InvalidRefreshTokenException("Invalid refresh token structure", e);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException("Failed to decode refresh token", e);
        }
    }
}
