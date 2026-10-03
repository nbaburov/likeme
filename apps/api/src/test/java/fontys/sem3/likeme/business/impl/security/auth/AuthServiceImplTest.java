package fontys.sem3.likeme.business.impl.security.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fontys.sem3.likeme.business.exception.security.auth.*;
import fontys.sem3.likeme.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.user.admin.AdminNotFoundException;
import fontys.sem3.likeme.business.exception.user.influencer.InfluencerNotFoundException;
import fontys.sem3.likeme.business.interfaces.security.PasswordService;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.business.interfaces.user.admin.AdminService;
import fontys.sem3.likeme.business.interfaces.user.client.ClientService;
import fontys.sem3.likeme.business.interfaces.user.influencer.InfluencerService;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.security.jwt.TokenResponse;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.User;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
        @Mock
        private AdminService adminService;
        @Mock
        private InfluencerService influencerService;
        @Mock
        private ClientService clientService;
        @Mock
        private PasswordService passwordService;
        @Mock
        private JwtService jwtService;

        @InjectMocks
        private AuthServiceImpl authService;

        private static final String USERNAME = "testuser";
        private static final String PASSWORD = "testpass";
        private static final String TOKEN_STRING = "token.test.string";
        private static final Long ADMIN_ID = 1L;

        private Admin testAdmin;
        private AccessToken testAccessToken;
        private RefreshToken testRefreshToken;
        private TokenResponse testTokenResponse;

        @BeforeEach
        void setUp() {
                testAdmin = Admin.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .isActive(true)
                                .build();

                testAccessToken = AccessToken.builder()
                                .token(TOKEN_STRING)
                                .subject(USERNAME)
                                .userId(ADMIN_ID)
                                .role(testAdmin.getRole())
                                .build();

                testRefreshToken = RefreshToken.builder()
                                .token(TOKEN_STRING)
                                .subject(USERNAME)
                                .userId(ADMIN_ID)
                                .build();

                testTokenResponse = TokenResponse.builder()
                                .accessToken(testAccessToken)
                                .refreshToken(testRefreshToken)
                                .build();
        }

        @Test
        void authenticate_Success_Admin() {
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);
                when(passwordService.verifyPassword(PASSWORD, testAdmin.getPassword())).thenReturn(true);
                when(jwtService.generateToken(testAdmin)).thenReturn(testAccessToken);
                when(jwtService.generateRefreshToken(testAdmin)).thenReturn(testRefreshToken);

                TokenResponse response = authService.authenticate(USERNAME, PASSWORD);

                assertNotNull(response);
                assertEquals(testAccessToken, response.getAccessToken());
                assertEquals(testRefreshToken, response.getRefreshToken());
                verify(adminService).getAdminByUsername(USERNAME);
                verify(passwordService).verifyPassword(PASSWORD, testAdmin.getPassword());
                verify(jwtService).generateToken(testAdmin);
                verify(jwtService).generateRefreshToken(testAdmin);
                verify(adminService).updateAdmin(testAdmin);
        }

        @Test
        void authenticate_NullUsername_ThrowsInvalidCredentialsException() {
                InvalidCredentialsException exception = assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.authenticate(null, PASSWORD));

                verify(adminService, never()).getAdminByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword(PASSWORD, testAdmin.getPassword());
        }

        @Test
        void authenticate_EmptyUsername_ThrowsInvalidCredentialsException() {
                InvalidCredentialsException exception = assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.authenticate("", PASSWORD));

                verify(adminService, never()).getAdminByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword(PASSWORD, testAdmin.getPassword());
        }

        @Test
        void authenticate_NullPassword_ThrowsInvalidCredentialsException() {
                InvalidCredentialsException exception = assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.authenticate(USERNAME, null));

                verify(adminService, never()).getAdminByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword(null, testAdmin.getPassword());
        }

        @Test
        void authenticate_EmptyPassword_ThrowsInvalidCredentialsException() {
                InvalidCredentialsException exception = assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.authenticate(USERNAME, ""));

                verify(adminService, never()).getAdminByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword("", testAdmin.getPassword());
        }

        @Test
        void authenticate_InactiveUser_ThrowsUserNotActiveException() {
                testAdmin.setIsActive(false);
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);

                UserNotActiveException exception = assertThrows(
                                UserNotActiveException.class,
                                () -> authService.authenticate(USERNAME, PASSWORD));

                verify(adminService).getAdminByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword(PASSWORD, testAdmin.getPassword());
        }

        @Test
        void authenticate_InvalidPassword_ThrowsInvalidCredentialsException() {
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);
                when(passwordService.verifyPassword(PASSWORD, testAdmin.getPassword())).thenReturn(false);

                InvalidCredentialsException exception = assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.authenticate(USERNAME, PASSWORD));

                verify(adminService).getAdminByUsername(USERNAME);
                verify(passwordService).verifyPassword(PASSWORD, testAdmin.getPassword());
        }

        @Test
        void refreshToken_Success() {
                when(jwtService.decodeRefreshToken(TOKEN_STRING)).thenReturn(testRefreshToken);
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);
                when(jwtService.generateToken(testAdmin)).thenReturn(testAccessToken);
                when(jwtService.generateRefreshToken(testAdmin)).thenReturn(testRefreshToken);

                TokenResponse response = authService.refreshToken(TOKEN_STRING);

                assertNotNull(response);
                assertEquals(testAccessToken, response.getAccessToken());
                assertEquals(testRefreshToken, response.getRefreshToken());
                verify(jwtService).decodeRefreshToken(TOKEN_STRING);
                verify(adminService).getAdminByUsername(USERNAME);
                verify(jwtService).generateToken(testAdmin);
                verify(jwtService).generateRefreshToken(testAdmin);
        }

        @Test
        void refreshToken_NullToken_ThrowsInvalidRefreshTokenException() {
                InvalidRefreshTokenException exception = assertThrows(
                                InvalidRefreshTokenException.class,
                                () -> authService.refreshToken(null));

                verify(jwtService, never()).decodeRefreshToken(TOKEN_STRING);
        }

        @Test
        void refreshToken_EmptyToken_ThrowsInvalidRefreshTokenException() {
                InvalidRefreshTokenException exception = assertThrows(
                                InvalidRefreshTokenException.class,
                                () -> authService.refreshToken(""));

                verify(jwtService, never()).decodeRefreshToken(TOKEN_STRING);
        }

        @Test
        void refreshToken_ExpiredToken_ThrowsExpiredTokenException() {
                when(jwtService.decodeRefreshToken(TOKEN_STRING))
                                .thenThrow(new ExpiredTokenException("Token has expired"));

                ExpiredTokenException exception = assertThrows(
                                ExpiredTokenException.class,
                                () -> authService.refreshToken(TOKEN_STRING));

                verify(jwtService).decodeRefreshToken(TOKEN_STRING);
                verify(adminService, never()).getAdminByUsername(USERNAME);
        }

        @Test
        void refreshToken_UserNotFound_ThrowsInvalidRefreshTokenException() {
                when(jwtService.decodeRefreshToken(TOKEN_STRING)).thenReturn(testRefreshToken);
                when(adminService.getAdminByUsername(USERNAME))
                                .thenThrow(new AdminNotFoundException("Admin not found"));
                when(influencerService.getInfluencerUsername(USERNAME))
                                .thenThrow(new InfluencerNotFoundException("Influencer not found"));
                when(clientService.getClientByUsername(USERNAME))
                                .thenReturn(null);

                InvalidRefreshTokenException exception = assertThrows(
                                InvalidRefreshTokenException.class,
                                () -> authService.refreshToken(TOKEN_STRING));

                assertEquals("User not found", exception.getMessage());
                verify(jwtService).decodeRefreshToken(TOKEN_STRING);
                verify(adminService).getAdminByUsername(USERNAME);
                verify(influencerService).getInfluencerUsername(USERNAME);
                verify(clientService).getClientByUsername(USERNAME);
        }

        @Test
        void authenticate_Success_Influencer() {
                Influencer testInfluencer = Influencer.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .isActive(true)
                                .build();

                when(adminService.getAdminByUsername(USERNAME))
                                .thenThrow(new AdminNotFoundException("Admin not found"));
                when(influencerService.getInfluencerUsername(USERNAME)).thenReturn(testInfluencer);
                when(passwordService.verifyPassword(PASSWORD, testInfluencer.getPassword())).thenReturn(true);
                when(jwtService.generateToken(testInfluencer)).thenReturn(testAccessToken);
                when(jwtService.generateRefreshToken(testInfluencer)).thenReturn(testRefreshToken);

                TokenResponse response = authService.authenticate(USERNAME, PASSWORD);

                assertNotNull(response);
                assertEquals(testAccessToken, response.getAccessToken());
                assertEquals(testRefreshToken, response.getRefreshToken());
                verify(influencerService).updateInfluencer(testInfluencer);
        }

        @Test
        void authenticate_Success_Client() {
                Client testClient = Client.builder()
                                .id(ADMIN_ID)
                                .username(USERNAME)
                                .password(PASSWORD)
                                .isActive(true)
                                .build();

                when(adminService.getAdminByUsername(USERNAME))
                                .thenThrow(new AdminNotFoundException("Admin not found"));
                when(influencerService.getInfluencerUsername(USERNAME))
                                .thenThrow(new InfluencerNotFoundException("Influencer not found"));
                when(clientService.getClientByUsername(USERNAME)).thenReturn(testClient);
                when(passwordService.verifyPassword(PASSWORD, testClient.getPassword())).thenReturn(true);
                when(jwtService.generateToken(testClient)).thenReturn(testAccessToken);
                when(jwtService.generateRefreshToken(testClient)).thenReturn(testRefreshToken);

                TokenResponse response = authService.authenticate(USERNAME, PASSWORD);

                assertNotNull(response);
                assertEquals(testAccessToken, response.getAccessToken());
                assertEquals(testRefreshToken, response.getRefreshToken());
                verify(clientService).updateClient(testClient);
        }

        @Test
        void authenticate_UserNotFound_ThrowsAuthenticationException() {
                when(adminService.getAdminByUsername(USERNAME))
                                .thenThrow(new AdminNotFoundException("Admin not found"));
                when(influencerService.getInfluencerUsername(USERNAME))
                                .thenThrow(new InfluencerNotFoundException("Influencer not found"));
                when(clientService.getClientByUsername(USERNAME))
                                .thenReturn(null);

                AuthenticationException exception = assertThrows(
                                AuthenticationException.class,
                                () -> authService.authenticate(USERNAME, PASSWORD));

                assertEquals("User not found", exception.getMessage());

                verify(adminService).getAdminByUsername(USERNAME);
                verify(influencerService).getInfluencerUsername(USERNAME);
                verify(clientService).getClientByUsername(USERNAME);
                verify(passwordService, never()).verifyPassword(any(), any());
        }

        @Test
        void authenticate_UpdateLastLoginFails_ThrowsAuthenticationException() {
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);
                when(passwordService.verifyPassword(PASSWORD, testAdmin.getPassword())).thenReturn(true);
                when(adminService.updateAdmin(testAdmin))
                                .thenThrow(new RuntimeException("Database error"));

                AuthenticationException exception = assertThrows(
                                AuthenticationException.class,
                                () -> authService.authenticate(USERNAME, PASSWORD));

                assertEquals("Authentication successful but failed to update last login date", exception.getMessage());
        }

        @Test
        void refreshToken_InactiveUser_ThrowsUserNotActiveException() {
                testAdmin.setIsActive(false);
                when(jwtService.decodeRefreshToken(TOKEN_STRING)).thenReturn(testRefreshToken);
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(testAdmin);

                UserNotActiveException exception = assertThrows(
                                UserNotActiveException.class,
                                () -> authService.refreshToken(TOKEN_STRING));

                verify(jwtService).decodeRefreshToken(TOKEN_STRING);
                verify(adminService).getAdminByUsername(USERNAME);
        }

        @Test
        void authenticate_UnknownUserType_ThrowsAuthenticationException() {
                // Create a mock Admin
                Admin mockAdmin = mock(Admin.class);
                when(mockAdmin.getPassword()).thenReturn(PASSWORD);
                when(mockAdmin.getIsActive()).thenReturn(true);

                // Setup the service mocks
                when(adminService.getAdminByUsername(USERNAME)).thenReturn(mockAdmin);
                when(passwordService.verifyPassword(PASSWORD, mockAdmin.getPassword())).thenReturn(true);
                doThrow(new ClassCastException()).when(adminService).updateAdmin(mockAdmin);

                // Act & Assert
                AuthenticationException exception = assertThrows(
                                AuthenticationException.class,
                                () -> authService.authenticate(USERNAME, PASSWORD));

                assertEquals("Authentication successful but failed to update last login date", exception.getMessage());

                // Verify only the necessary interactions
                verify(adminService).getAdminByUsername(USERNAME);
                verify(passwordService).verifyPassword(PASSWORD, mockAdmin.getPassword());
                verify(adminService).updateAdmin(mockAdmin);
        }
}