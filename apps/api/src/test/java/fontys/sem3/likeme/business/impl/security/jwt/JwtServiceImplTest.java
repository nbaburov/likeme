package fontys.sem3.likeme.business.impl.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import fontys.sem3.likeme.business.exception.security.auth.ExpiredTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidRefreshTokenException;
import fontys.sem3.likeme.business.exception.security.jwt.JwtServiceException;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.user.Role;
import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.domain.user.client.Client;
import fontys.sem3.likeme.domain.user.influencer.Influencer;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long ACCESS_TOKEN_EXPIRATION = 3600000; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION = 86400000; // 24 hours
    private static final String USERNAME = "testuser";
    private static final Long USER_ID = 1L;

    private Admin testAdmin;
    private Client testClient;
    private Influencer testInfluencer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", ACCESS_TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);

        testAdmin = Admin.builder()
                .id(USER_ID)
                .username(USERNAME)
                .isActive(true)
                .role(Role.ADMIN)
                .build();

        testClient = Client.builder()
                .id(USER_ID)
                .username(USERNAME)
                .isActive(true)
                .role(Role.CLIENT)
                .build();

        InfluencerApplication application = InfluencerApplication.builder()
                .username(USERNAME)
                .build();

        testInfluencer = Influencer.builder()
                .id(USER_ID)
                .isActive(true)
                .role(Role.INFLUENCER)
                .application(application)
                .build();
    }

    @Test
    void generateToken_Admin_Success() {
        AccessToken token = jwtService.generateToken(testAdmin);

        assertNotNull(token);
        assertEquals(USERNAME, token.getSubject());
        assertEquals(USER_ID, token.getUserId());
        assertEquals(Role.ADMIN, token.getRole());
        assertNotNull(token.getToken());
    }

    @Test
    void generateToken_Client_Success() {
        AccessToken token = jwtService.generateToken(testClient);

        assertNotNull(token);
        assertEquals(USERNAME, token.getSubject());
        assertEquals(USER_ID, token.getUserId());
        assertEquals(Role.CLIENT, token.getRole());
        assertNotNull(token.getToken());
    }

    @Test
    void generateToken_Influencer_Success() {
        AccessToken token = jwtService.generateToken(testInfluencer);

        assertNotNull(token);
        assertEquals(USERNAME, token.getSubject());
        assertEquals(USER_ID, token.getUserId());
        assertEquals(Role.INFLUENCER, token.getRole());
        assertNotNull(token.getToken());
    }

    @Test
    void generateToken_NullUser_ThrowsJwtServiceException() {
        assertThrows(JwtServiceException.class,
                () -> jwtService.generateToken(null));
    }

    @Test
    void generateRefreshToken_Admin_Success() {
        RefreshToken token = jwtService.generateRefreshToken(testAdmin);

        assertNotNull(token);
        assertEquals(USERNAME, token.getSubject());
        assertEquals(USER_ID, token.getUserId());
        assertNotNull(token.getToken());
    }

    @Test
    void generateRefreshToken_NullUser_ThrowsJwtServiceException() {
        assertThrows(JwtServiceException.class,
                () -> jwtService.generateRefreshToken(null));
    }

    @Test
    void decodeRefreshToken_ValidToken_Success() {
        RefreshToken originalToken = jwtService.generateRefreshToken(testAdmin);
        RefreshToken decodedToken = jwtService.decodeRefreshToken(originalToken.getToken());

        assertNotNull(decodedToken);
        assertEquals(originalToken.getSubject(), decodedToken.getSubject());
        assertEquals(originalToken.getUserId(), decodedToken.getUserId());
    }

    @Test
    void decodeRefreshToken_ExpiredToken_ThrowsExpiredTokenException() {
        // Set a very short expiration time for testing
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", -1000L);

        RefreshToken originalToken = jwtService.generateRefreshToken(testAdmin);

        assertThrows(ExpiredTokenException.class,
                () -> jwtService.decodeRefreshToken(originalToken.getToken()));
    }

    @Test
    void decodeRefreshToken_InvalidToken_ThrowsInvalidRefreshTokenException() {
        assertThrows(InvalidRefreshTokenException.class,
                () -> jwtService.decodeRefreshToken("invalid.token.string"));
    }

    @Test
    void decodeRefreshToken_NullToken_ThrowsInvalidRefreshTokenException() {
        assertThrows(InvalidRefreshTokenException.class,
                () -> jwtService.decodeRefreshToken(null));
    }

    @Test
    void decodeRefreshToken_EmptyToken_ThrowsInvalidRefreshTokenException() {
        assertThrows(InvalidRefreshTokenException.class,
                () -> jwtService.decodeRefreshToken(""));
    }
}