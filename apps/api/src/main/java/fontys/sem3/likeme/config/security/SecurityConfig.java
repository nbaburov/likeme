package fontys.sem3.likeme.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import fontys.sem3.likeme.config.security.jwt.JwtFilter;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig {

        private static final String[] PUBLIC_PATHS = {
                        "/docs/**",
                        "/auth/**",
                        "/ws/**",
                        "/ws-influencer.html",
                        "/ws-client.html",
                        "/static/**",
                        "/*.html",
                        "/*.js",
                        "/*.css"
        };

        @Value("${spring.profiles.active:}")
        private String activeProfile;

        @Value("${cors.allowed-origins}")
        private String corsAllowedOrigins;

        @Value("${cors.allowed-methods}")
        private String corsAllowedMethods;

        @Value("${cors.allowed-headers}")
        private String corsAllowedHeaders;

        @Value("${cors.max-age}")
        private int corsMaxAge;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                        AuthenticationEntryPoint authenticationEntryPoint,
                        AccessDeniedHandler accessDeniedHandler,
                        JwtFilter jwtFilter) throws Exception {
                HttpSecurity chain = httpSecurity
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .sessionManagement(configurer -> configurer
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(registry -> registry
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(PUBLIC_PATHS).permitAll()
                                                // Clients
                                                .requestMatchers(HttpMethod.POST, "/clients").permitAll()
                                                // Influencers
                                                .requestMatchers(HttpMethod.GET, "/influencers")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/influencers/*")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/influencers/applications")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/influencers/setup/account")
                                                .permitAll()
                                                // Files
                                                .requestMatchers(HttpMethod.GET, "/files/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/files/**")
                                                .permitAll()
                                                // Offers
                                                .requestMatchers(HttpMethod.GET, "/offers/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                if (activeProfile.equals("test")) {
                        chain.csrf(AbstractHttpConfigurer::disable);
                        chain.cors(AbstractHttpConfigurer::disable);
                }
                return chain.build();
        }

        @Bean
        public WebMvcConfigurer corsConfigurer() {
                return new WebMvcConfigurer() {
                        @Override
                        public void addCorsMappings(CorsRegistry registry) {
                                registry.addMapping("/**")
                                                .allowedOrigins(corsAllowedOrigins)
                                                .allowedMethods(corsAllowedMethods)
                                                .allowedHeaders(corsAllowedHeaders)
                                                .maxAge(corsMaxAge);
                        }
                };
        }
}
