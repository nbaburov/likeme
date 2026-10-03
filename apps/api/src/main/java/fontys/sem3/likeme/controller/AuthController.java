package fontys.sem3.likeme.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fontys.sem3.likeme.business.converter.security.auth.AuthConverter;
import fontys.sem3.likeme.business.interfaces.security.auth.AuthService;
import fontys.sem3.likeme.controller.dto.auth.AuthRequest;
import fontys.sem3.likeme.controller.dto.auth.AuthResponse;
import fontys.sem3.likeme.controller.dto.auth.RefreshTokenRequest;
import fontys.sem3.likeme.domain.security.jwt.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Endpoints for authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return access and refresh tokens")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        TokenResponse tokens = authService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(AuthConverter.mapToResponse(tokens));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Get new access and refresh tokens using refresh token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        TokenResponse tokens = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(AuthConverter.mapToResponse(tokens));
    }
}
