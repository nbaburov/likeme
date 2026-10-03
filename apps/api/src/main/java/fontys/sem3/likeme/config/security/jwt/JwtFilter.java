package fontys.sem3.likeme.config.security.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import fontys.sem3.likeme.business.exception.security.auth.ExpiredTokenException;
import fontys.sem3.likeme.business.exception.security.auth.InvalidTokenException;
import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.controller.dto.error.ErrorResponseDTO;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                chain.doFilter(request, response);
                return;
            }

            String accessTokenString = requestTokenHeader.substring(7);
            log.debug("Decoding token: {}", accessTokenString);

            AccessToken accessToken = jwtService.decodeToken(accessTokenString);
            log.debug("Decoded token: {}", accessToken);
            setupSpringSecurityContext(accessToken);
            chain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            log.warn("Invalid token received", e);
            sendAuthenticationError(response, "Invalid access token: " + e.getMessage());
        } catch (ExpiredTokenException e) {
            log.warn("Token expired", e);
            sendAuthenticationError(response, "Token has expired: " + e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT processing error", e);
            sendAuthenticationError(response, "JWT processing error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Authentication error", e);
            sendAuthenticationError(response, "Authentication failed: " + e.getMessage());
        }
    }

    private void sendAuthenticationError(HttpServletResponse response, String message) throws IOException {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            "AUTHENTICATION_ERROR",
            "Authentication Failed",
            message,
            LocalDateTime.now().format(formatter)
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.flushBuffer();
    }

    private void setupSpringSecurityContext(AccessToken accessToken) {
        String role = accessToken.getRole().name();
        if (!role.startsWith(SPRING_SECURITY_ROLE_PREFIX)) {
            role = SPRING_SECURITY_ROLE_PREFIX + role;
        }
        log.debug("Setting up security context with role: {}", role);

        Collection<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(role));

        UserDetails userDetails = User.builder()
                .username(accessToken.getSubject())
                .password("")
                .authorities(authorities)
                .build();
        log.debug("Created UserDetails with authorities: {}", authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authorities);
        authentication.setDetails(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
