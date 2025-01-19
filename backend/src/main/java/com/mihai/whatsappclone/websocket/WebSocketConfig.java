package com.mihai.whatsappclone.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * WebSocketConfig configures WebSocket communication in the application.
 * It sets up message brokers, STOMP endpoints, argument resolvers, and message converters.
 */
@Configuration
@EnableWebSocket
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Ensures this configuration is applied early
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker settings.
     *
     * @param registry The MessageBrokerRegistry object used to configure the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple broker for user-specific messages.
        registry.enableSimpleBroker("/user");

        // Set the application destination prefixes. Messages that start with "/app" will be routed to application controllers.
        registry.setApplicationDestinationPrefixes("/app");

        // Set the user destination prefix for user-specific messages.
        registry.setUserDestinationPrefix("/users");
    }

    /**
     * Registers STOMP endpoints to enable WebSocket connections.
     *
     * @param registry The StompEndpointRegistry object used to configure the WebSocket endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws" endpoint for WebSocket connections.
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200") // Allow connections from Angular client running on this port.
                .withSockJS(); // Use SockJS to provide fallback options for clients that don't support WebSocket.
    }

    /**
     * Adds argument resolvers to handle specific arguments for controller methods.
     *
     * @param argumentResolvers The list of argument resolvers.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // Add the AuthenticationPrincipalArgumentResolver to inject the authenticated user into controller methods.
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    /**
     * Configures message converters to convert between message payloads and Java objects.
     *
     * @param messageConverters The list of message converters.
     * @return false to prevent overriding default converters.
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        // Create a content type resolver with a default MIME type of application/json.
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON);

        // Create a Jackson message converter for converting Java objects to JSON.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper()); // Use Jackson's ObjectMapper for JSON conversion.
        converter.setContentTypeResolver(resolver); // Set the resolver for content type.

        // Add the message converter to the list.
        messageConverters.add(converter);

        return false; // Return false to prevent overriding default message converters.
    }
}
