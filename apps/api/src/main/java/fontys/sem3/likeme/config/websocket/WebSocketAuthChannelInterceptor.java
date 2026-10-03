package fontys.sem3.likeme.config.websocket;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import fontys.sem3.likeme.business.interfaces.security.jwt.JwtService;
import fontys.sem3.likeme.domain.security.jwt.AccessToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Retrieve the STOMP header accessor from the message
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        // Check if the request is a valid subscription request
        if (!isValidSubscribeRequest(accessor)) {
            return message; // If not valid, return the original message
        }

        // Get the Authorization header from the STOMP message
        String authorization = accessor.getFirstNativeHeader("Authorization");
        // Validate the Authorization header format
        if (!isValidAuthorizationHeader(authorization)) {
            return message; // If invalid, return the original message
        }

        // Process authentication if the request is valid
        return processAuthentication(message, accessor, authorization);
    }

    private boolean isValidSubscribeRequest(StompHeaderAccessor accessor) {
        // Check if the accessor is not null and the command is a SUBSCRIBE command
        return accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand());
    }

    private boolean isValidAuthorizationHeader(String authorization) {
        // Validate that the Authorization header is present and starts with "Bearer "
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private Message<?> processAuthentication(Message<?> message, StompHeaderAccessor accessor, String authorization) {
        try {
            // Decode the JWT token from the Authorization header
            AccessToken accessToken = jwtService.decodeToken(authorization.substring(7));
            
            // Validate the subscription based on the destination and access token
            if (!validateSubscription(accessor.getDestination(), accessToken)) {
                return null; // If validation fails, return null to reject the subscription
            }

            // Set the authentication token in the STOMP header accessor
            setAuthenticationToken(accessor, accessToken);
            logger.info("WebSocket connection authenticated for user: {}", accessToken.getSubject());
            return message; // Return the original message after successful authentication
        } catch (Exception e) {
            logger.error("WebSocket authentication failed: {}", e.getMessage());
            return null; // Return null if authentication fails
        }
    }

    private boolean validateSubscription(String destination, AccessToken accessToken) {
        // Allow null destination for general access
        if (destination == null) {
            return true;
        }

        // Validate role-based access for the subscription
        if (!validateRoleBasedAccess(destination, accessToken.getRole().name())) {
            return false; // Deny access if role validation fails
        }

        // Validate that the user ID in the token matches the requested user ID in the destination
        return validateUserId(destination, accessToken.getUserId());
    }

    private boolean validateRoleBasedAccess(String destination, String role) {
        // Check if the destination is for an influencer and validate the role
        if (destination.startsWith("/user/") && destination.contains("/influencer/")) {
            if (!"INFLUENCER".equals(role)) {
                logger.error("Unauthorized subscription attempt to influencer channel");
                return false; // Deny access if the role does not match
            }
        } 
        // Check if the destination is for a client and validate the role
        else if (destination.startsWith("/user/") && destination.contains("/client/") && !"CLIENT".equals(role)) {
            logger.error("Unauthorized subscription attempt to client channel");
            return false; // Deny access if the role does not match
        }

        return true; // Allow access if all checks pass
    }

    private boolean validateUserId(String destination, Long tokenUserId) {
        // Split the destination to extract the requested user ID
        String[] parts = destination.split("/");
        if (parts.length >= 3) {
            String requestedUserId = parts[2];
            // Check if the requested user ID matches the user ID in the token
            if (!requestedUserId.equals(tokenUserId.toString())) {
                logger.error("User ID mismatch in subscription");
                return false; // Deny access if user IDs do not match
            }
        }
        return true; // Allow access if user ID matches
    }

    private void setAuthenticationToken(StompHeaderAccessor accessor, AccessToken accessToken) {
        // Create an authentication token with the user's details and roles
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            accessToken.getSubject(),
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessToken.getRole().name()))
        );
        // Set the authentication token in the STOMP header accessor
        accessor.setUser(auth);
    }
}