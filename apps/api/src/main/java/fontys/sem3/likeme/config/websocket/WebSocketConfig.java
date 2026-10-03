package fontys.sem3.likeme.config.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final WebSocketAuthChannelInterceptor authChannelInterceptor; // Interceptor for authenticating WebSocket messages

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configures the message broker to enable simple broker for topics and queues
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app"); // Prefix for application destinations
        config.setUserDestinationPrefix("/user"); // Prefix for user-specific destinations
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the STOMP endpoint for WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allows all origins for CORS
                .withSockJS(); // Enables SockJS fallback options for browsers that don't support WebSocket
        logger.info("STOMP endpoint registered at /ws"); // Logs the registration of the STOMP endpoint
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Registers the authentication channel interceptor for inbound messages
        registration.interceptors(authChannelInterceptor);
    }
}