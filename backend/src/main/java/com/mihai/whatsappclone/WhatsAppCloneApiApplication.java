package com.mihai.whatsappclone;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the WhatsApp Clone API application.
 * Configures Spring Boot, JPA auditing, and OpenAPI security schemes for Keycloak.
 */
@SpringBootApplication // Marks this as a Spring Boot application.
@EnableJpaAuditing // Enables JPA auditing for tracking entity changes (e.g., createdDate, lastModifiedDate).
@SecurityScheme( // Defines the OpenAPI security scheme for OAuth2 integration with Keycloak.
		name = "keycloak", // Name of the security scheme (referenced in OpenAPI configurations).
		type = SecuritySchemeType.OAUTH2, // Specifies OAuth2 as the security scheme type.
		bearerFormat = "JWT", // Indicates that the authentication uses JWT tokens.
		scheme = "bearer", // Specifies Bearer Token as the authentication scheme.
		in = SecuritySchemeIn.HEADER, // Specifies that the token is passed in the HTTP header.
		flows = @OAuthFlows( // Configures the OAuth2 flow for authentication and token retrieval.
				password = @OAuthFlow(
						authorizationUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/auth", // Keycloak authorization endpoint.
						tokenUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/token" // Keycloak token endpoint.
				)
		)
)
public class WhatsAppCloneApiApplication {

	/**
	 * The main method that serves as the entry point for the Spring Boot application.
	 *
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(WhatsAppCloneApiApplication.class, args); // Launches the application.
	}
}
