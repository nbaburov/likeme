package fontys.sem3.likeme.business.impl.security.auth;

import java.util.Date;

import org.springframework.stereotype.Service;

import fontys.sem3.likeme.business.exception.security.auth.AuthenticationException;
import fontys.sem3.likeme.business.exception.security.auth.ExpiredTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidCredentialsException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidRefreshTokenException;
import fontys.sem3.likeme.business.exception.security.auth.RefreshTokenException;
import fontys.sem3.likeme.business.exception.security.auth.UserNotActiveException;
import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.exception.user.client.ClientNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.security.auth.AuthService;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.security.jwt.TokenResponse;
import fontys.sem3.likeme.domain.user.User;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AdminService adminService;
    private final InfluencerService influencerService;
    private final ClientService clientService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Override
    public TokenResponse authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidCredentialsException();
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException();
        }

        try {
            User user = findUserByUsername(username);

            if (user == null) {
                throw new AuthenticationException("User not found");
            }

            if (Boolean.FALSE.equals(user.getIsActive())) {
                throw new UserNotActiveException();
            }

            if (!passwordService.verifyPassword(password, user.getPassword())) {
                throw new InvalidCredentialsException();
            }

            updateUserLastLogin(user);

            return TokenResponse.builder()
                    .accessToken(jwtService.generateToken(user))
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .build();
        } catch (UserNotActiveException | InvalidCredentialsException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed", e);
        }
    }

    private void updateUserLastLogin(User user) {
        user.setLastLoginOn(new Date());
        try {
            if (user instanceof Admin admin) {
                adminService.updateAdmin(admin);
            } else if (user instanceof Influencer influencer) {
                influencerService.updateInfluencer(influencer);
            } else if (user instanceof Client client) {
                clientService.updateClient(client);
            } else {
                throw new AuthenticationException("Unknown user type");
            }
        } catch (AdminNotFoundException | InfluencerNotFoundException e) {
            throw new AuthenticationException("User not found during last login update", e);
        } catch (Exception e) {
            throw new AuthenticationException("Authentication successful but failed to update last login date", e);
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new InvalidRefreshTokenException("Refresh token cannot be empty");
        }

        try {
            RefreshToken decodedRefreshToken = jwtService.decodeRefreshToken(refreshToken);
            User user = findUserByUsername(decodedRefreshToken.getSubject());

            if (user == null) {
                throw new InvalidRefreshTokenException("User not found");
            }

            if (Boolean.FALSE.equals(user.getIsActive())) {
                throw new UserNotActiveException();
            }

            return TokenResponse.builder()
                    .accessToken(jwtService.generateToken(user))
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .build();
        } catch (InvalidRefreshTokenException | ExpiredTokenException | UserNotActiveException e) {
            throw e;
        } catch (Exception e) {
            throw new RefreshTokenException("Failed to refresh token", e);
        }
    }

    private User findUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidCredentialsException();
        }

        User user = null;

        try {
            user = adminService.getAdminByUsername(username);
            if (user != null)
                return user;
        } catch (AdminNotFoundException ignored) {
            // Continue to try other user types
        }

        try {
            user = influencerService.getInfluencerUsername(username);
            if (user != null)
                return user;
        } catch (InfluencerNotFoundException ignored) {
            // Continue to try other user types
        }

        try {
            user = clientService.getClientByUsername(username);
            if (user != null)
                return user;
        } catch (ClientNotFoundException ignored) {
            // Continue to try other user types
        }

        return null; // No user found in any service
    }
}
