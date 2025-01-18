package com.mihai.whatsappclone.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Custom JWT Authentication Converter for Keycloak integration.
 * Converts a JWT into an AbstractAuthenticationToken by extracting roles from Keycloak's `resource_access` claim.
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Converts a JWT into an AbstractAuthenticationToken by combining default granted authorities
     * with roles extracted from Keycloak's resource access claims.
     *
     * @param source the incoming JWT to be converted.
     * @return a JwtAuthenticationToken containing authorities and token details.
     */
    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        return new JwtAuthenticationToken(
                source,
                Stream.concat(
                                // Combine default authorities (from JWT scope claims) with Keycloak roles.
                                new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                                extractResourceRoles(source).stream())
                        .collect(toSet())); // Collect authorities into a set to eliminate duplicates.
    }

    /**
     * Extracts roles from the Keycloak `resource_access` claim and converts them to GrantedAuthority objects.
     *
     * @param jwt the incoming JWT containing claims.
     * @return a collection of GrantedAuthority objects representing roles in the `resource_access` claim.
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        // Extract the `resource_access` claim as a map.
        var resourceAccess = new HashMap<>(jwt.getClaim("resource_access"));

        // Retrieve roles for the "account" resource from the `resource_access` claim.
        var eternal = (Map<String, List<String>>) resourceAccess.get("account");
        var roles = eternal.get("roles");

        // Convert roles to GrantedAuthority objects, prefixing each role with "ROLE_".
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_"))) // Replace dashes with underscores for consistency.
                .collect(toSet()); // Collect into a set to ensure uniqueness.
    }
}
