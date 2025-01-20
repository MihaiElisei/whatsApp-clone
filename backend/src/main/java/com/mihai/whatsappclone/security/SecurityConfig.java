package com.mihai.whatsappclone.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration class that sets up Spring Security and CORS policies for the application.
 */
@Configuration
@EnableWebSecurity // Enables Spring Security in the application.
public class SecurityConfig {

    /**
     * Configures the Security Filter Chain for handling HTTP requests.
     *
     * @param http the HttpSecurity object to configure.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Enables default Cross-Origin Resource Sharing (CORS) configuration.
                .cors(withDefaults())
                // Disables CSRF protection (not needed for APIs with JWT-based authentication).
                .csrf(AbstractHttpConfigurer::disable)
                // Configures authorization for HTTP requests.
                .authorizeHttpRequests(req ->
                        req
                                // Allows unauthenticated access to certain endpoints (e.g., authentication and Swagger).
                                .requestMatchers(
                                        "/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/ws/**"
                                ).permitAll()
                                // Requires authentication for all other requests.
                                .anyRequest().authenticated()
                )
                // Configures the application as an OAuth2 resource server with JWT authentication.
                .oauth2ResourceServer(auth ->
                        auth.jwt(token ->
                                // Customizes the JWT authentication conversion logic using Keycloak.
                                token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter())));

        return http.build(); // Builds the configured security filter chain.
    }

    /**
     * Configures a custom CORS filter to handle cross-origin requests.
     *
     * @return the configured CorsFilter.
     */
    @Bean
    public CorsFilter corsFilter() {
        // Configures the URL-based CORS policy source.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        // Allows credentials (e.g., cookies or authorization headers) for cross-origin requests.
        config.setAllowCredentials(true);

        // Specifies the allowed origins for cross-origin requests.
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // Frontend origin.

        // Specifies the allowed headers for cross-origin requests.
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.ORIGIN,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.AUTHORIZATION
        ));

        // Specifies the allowed HTTP methods for cross-origin requests.
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "DELETE",
                "PUT",
                "PATCH"
        ));

        // Registers the CORS configuration for all endpoints.
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source); // Creates and returns the CORS filter.
    }
}
