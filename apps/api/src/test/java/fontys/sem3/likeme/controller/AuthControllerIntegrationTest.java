package fontys.sem3.likeme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fontys.sem3.likeme.business.interfaces.security.auth.AuthService;
import fontys.sem3.likeme.controller.dto.auth.AuthRequest;
import fontys.sem3.likeme.controller.dto.auth.RefreshTokenRequest;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import fontys.sem3.likeme.domain.security.jwt.RefreshToken;
import fontys.sem3.likeme.domain.security.jwt.TokenResponse;
import fontys.sem3.likeme.business.exception.security.auth.InvalidCredentialsException;
import fontys.sem3.likeme.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private TokenResponse validTokenResponse;
    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "Test123!@#";
    private static final String VALID_REFRESH_TOKEN = "valid.refresh.token";

    @BeforeEach
    void setUp() {
        AccessToken accessToken = AccessToken.builder()
                .token("valid.access.token")
                .subject(VALID_USERNAME)
                .userId(1L)
                .role(Role.CLIENT)
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(VALID_REFRESH_TOKEN)
                .subject(VALID_USERNAME)
                .userId(1L)
                .build();

        validTokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Test
    void login_ValidCredentials_ReturnsTokens() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();

        when(authService.authenticate(VALID_USERNAME, VALID_PASSWORD))
                .thenReturn(validTokenResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(validTokenResponse.getAccessToken().getToken()))
                .andExpect(jsonPath("$.refreshToken").value(validTokenResponse.getRefreshToken().getToken()));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .username("invalid")
                .password("invalid")
                .build();

        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void login_MissingCredentials_ReturnsBadRequest() throws Exception {
        AuthRequest request = AuthRequest.builder().build();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void refresh_ValidToken_ReturnsNewTokens() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(VALID_REFRESH_TOKEN)
                .build();

        when(authService.refreshToken(VALID_REFRESH_TOKEN))
                .thenReturn(validTokenResponse);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(validTokenResponse.getAccessToken().getToken()))
                .andExpect(jsonPath("$.refreshToken").value(validTokenResponse.getRefreshToken().getToken()));
    }

    @Test
    void refresh_InvalidToken_ReturnsUnauthorized() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid.token")
                .build();

        when(authService.refreshToken(anyString()))
                .thenThrow(new fontys.sem3.likeme.business.exception.security.auth.InvalidRefreshTokenException(
                        "Invalid refresh token"));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));
    }

    @Test
    void refresh_MissingToken_ReturnsBadRequest() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder().build();

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}