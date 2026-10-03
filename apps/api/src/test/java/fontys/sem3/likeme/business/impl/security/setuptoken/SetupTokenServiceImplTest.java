package fontys.sem3.likeme.business.impl.security.setuptoken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fontys.sem3.likeme.business.exception.security.setuptoken.InvalidSetupTokenException;
import fontys.sem3.likeme.business.exception.security.setuptoken.SetupTokenServiceException;
import fontys.sem3.likeme.domain.security.setuptoken.SetupToken;
import fontys.sem3.likeme.domain.user.influencer.InfluencerApplication;
import fontys.sem3.likeme.repository.interfaces.security.setuptoken.SetupTokenRepository;

@ExtendWith(MockitoExtension.class)
class SetupTokenServiceImplTest {
    @Mock
    private SetupTokenRepository setupTokenRepository;

    @InjectMocks
    private SetupTokenServiceImpl setupTokenService;

    private InfluencerApplication testApplication;
    private SetupToken testToken;
    private SetupToken expiredToken;
    private SetupToken usedToken;
    private static final Long APPLICATION_ID = 1L;
    private static final String TOKEN_STRING = "test-token-string";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final LocalDateTime EXPIRY_TIME = CURRENT_TIME.plusHours(24);
    private static final LocalDateTime EXPIRED_TIME = CURRENT_TIME.minusHours(1);

    @BeforeEach
    void setUp() {
        testApplication = InfluencerApplication.builder()
                .id(APPLICATION_ID)
                .build();

        testToken = SetupToken.builder()
                .id(1L)
                .application(testApplication)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRY_TIME)
                .isUsed(false)
                .createdOn(CURRENT_TIME)
                .build();

        expiredToken = SetupToken.builder()
                .id(1L)
                .application(testApplication)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRED_TIME)
                .isUsed(false)
                .createdOn(CURRENT_TIME)
                .build();

        usedToken = SetupToken.builder()
                .id(1L)
                .application(testApplication)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRY_TIME)
                .isUsed(true)
                .createdOn(CURRENT_TIME)
                .build();
    }

    @Test
    void createToken_ValidApplication_Success() {
        SetupToken expectedToken = SetupToken.builder()
                .application(testApplication)
                .expiresAt(EXPIRY_TIME)
                .isUsed(false)
                .createdOn(CURRENT_TIME)
                .build();

        when(setupTokenRepository.save(argThat(token -> token.getApplication().equals(testApplication) &&
                !token.getIsUsed() &&
                token.getExpiresAt().isAfter(CURRENT_TIME)))).thenReturn(testToken);

        SetupToken result = setupTokenService.createToken(testApplication);

        assertEquals(testToken.getId(), result.getId());
        assertEquals(testToken.getApplication(), result.getApplication());
        assertEquals(testToken.getToken(), result.getToken());
        assertEquals(testToken.getExpiresAt(), result.getExpiresAt());
        assertEquals(testToken.getIsUsed(), result.getIsUsed());
        assertEquals(testToken.getCreatedOn(), result.getCreatedOn());
    }

    @Test
    void validateToken_ValidToken_Success() {
        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.of(testToken));

        SetupToken result = setupTokenService.validateToken(TOKEN_STRING);

        assertEquals(testToken.getId(), result.getId());
        assertEquals(testToken.getApplication(), result.getApplication());
        assertEquals(testToken.getToken(), result.getToken());
        assertEquals(testToken.getExpiresAt(), result.getExpiresAt());
        assertEquals(testToken.getIsUsed(), result.getIsUsed());
        assertEquals(testToken.getCreatedOn(), result.getCreatedOn());
    }

    @Test
    void validateToken_ExpiredToken_ThrowsException() {
        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.of(expiredToken));

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken(TOKEN_STRING));

        assertEquals("Token has expired", exception.getMessage());
        assertTrue(expiredToken.getExpiresAt().isBefore(CURRENT_TIME));
    }

    @Test
    void validateToken_UsedToken_ThrowsException() {
        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.of(usedToken));

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken(TOKEN_STRING));

        assertEquals("Token has already been used", exception.getMessage());
        assertTrue(usedToken.getIsUsed());
    }

    @Test
    void invalidateToken_ValidToken_Success() {
        SetupToken tokenToInvalidate = SetupToken.builder()
                .id(1L)
                .application(testApplication)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRY_TIME)
                .isUsed(false)
                .createdOn(CURRENT_TIME)
                .build();

        SetupToken invalidatedToken = SetupToken.builder()
                .id(1L)
                .application(testApplication)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRY_TIME)
                .isUsed(true)
                .createdOn(CURRENT_TIME)
                .build();

        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.of(tokenToInvalidate));
        when(setupTokenRepository.save(argThat(token -> token.getIsUsed()))).thenReturn(invalidatedToken);

        setupTokenService.invalidateToken(TOKEN_STRING);

        verify(setupTokenRepository).save(argThat(token -> token.getId().equals(tokenToInvalidate.getId()) &&
                token.getApplication().equals(tokenToInvalidate.getApplication()) &&
                token.getToken().equals(tokenToInvalidate.getToken()) &&
                token.getExpiresAt().equals(tokenToInvalidate.getExpiresAt()) &&
                token.getIsUsed()));
    }

    @Test
    void deleteTokenByApplicationId_ValidId_Success() {
        setupTokenService.deleteTokenByApplicationId(APPLICATION_ID);

        verify(setupTokenRepository).deleteByApplicationId(APPLICATION_ID);
        verifyNoMoreInteractions(setupTokenRepository);
    }

    @Test
    void deleteTokenByApplicationId_RepositoryError_ThrowsException() {
        doThrow(new RuntimeException("Database error"))
                .when(setupTokenRepository).deleteByApplicationId(APPLICATION_ID);

        SetupTokenServiceException exception = assertThrows(
                SetupTokenServiceException.class,
                () -> setupTokenService.deleteTokenByApplicationId(APPLICATION_ID));

        assertEquals("Failed to delete setup token by application ID", exception.getMessage());
        verify(setupTokenRepository).deleteByApplicationId(APPLICATION_ID);
        verifyNoMoreInteractions(setupTokenRepository);
    }

    @Test
    void createToken_NullApplication_ThrowsException() {
        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.createToken(null));

        assertEquals("Invalid application data", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }

    @Test
    void createToken_ApplicationWithNullId_ThrowsException() {
        InfluencerApplication invalidApp = InfluencerApplication.builder()
                .id(null)
                .build();

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.createToken(invalidApp));

        assertEquals("Invalid application data", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }

    @Test
    void createToken_RepositoryError_ThrowsException() {
        when(setupTokenRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        SetupTokenServiceException exception = assertThrows(
                SetupTokenServiceException.class,
                () -> setupTokenService.createToken(testApplication));

        assertEquals("Failed to create setup token", exception.getMessage());
    }

    @Test
    void validateToken_NullToken_ThrowsException() {
        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken(null));

        assertEquals("Token cannot be null or empty", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }

    @Test
    void validateToken_EmptyToken_ThrowsException() {
        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken("  "));

        assertEquals("Token cannot be null or empty", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }

    @Test
    void validateToken_TokenNotFound_ThrowsException() {
        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.empty());

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken(TOKEN_STRING));

        assertEquals("Token not found or invalid", exception.getMessage());
    }

    @Test
    void validateToken_TokenWithNullApplication_ThrowsException() {
        SetupToken invalidToken = SetupToken.builder()
                .id(1L)
                .application(null)
                .token(TOKEN_STRING)
                .expiresAt(EXPIRY_TIME)
                .isUsed(false)
                .build();

        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.of(invalidToken));

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.validateToken(TOKEN_STRING));

        assertEquals("Token is not associated with a valid application", exception.getMessage());
    }

    @Test
    void invalidateToken_NullToken_ThrowsException() {
        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.invalidateToken(null));

        assertEquals("Token cannot be null", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }

    @Test
    void invalidateToken_TokenNotFound_ThrowsException() {
        when(setupTokenRepository.findByToken(TOKEN_STRING)).thenReturn(Optional.empty());

        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.invalidateToken(TOKEN_STRING));

        assertEquals("Token not found", exception.getMessage());
    }

    @Test
    void deleteTokenByApplicationId_NullId_ThrowsException() {
        InvalidSetupTokenException exception = assertThrows(
                InvalidSetupTokenException.class,
                () -> setupTokenService.deleteTokenByApplicationId(null));

        assertEquals("Application ID cannot be null", exception.getMessage());
        verifyNoInteractions(setupTokenRepository);
    }
}