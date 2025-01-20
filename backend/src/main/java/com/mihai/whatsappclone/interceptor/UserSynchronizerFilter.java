package com.mihai.whatsappclone.interceptor;

import com.mihai.whatsappclone.user.UserSynchronizer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filter is responsible for synchronizing the user data with the identity provider (IDP)
 * on every request, except for anonymous requests.
 * It checks the authentication token, and if it's valid, it synchronizes the user data.
 */
@Component
@RequiredArgsConstructor
public class UserSynchronizerFilter extends OncePerRequestFilter {

    /**
     * The user synchronizer service, which is responsible for syncing the user data.
     */
    private final UserSynchronizer userSynchronizer;

    /**
     * This method is executed for each request. It checks if the user is authenticated,
     * and if so, it synchronizes the user's data with the identity provider (IDP).
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain to continue the request processing.
     * @throws ServletException If the filter encounters an exception.
     * @throws IOException If there's an I/O error during the filtering process.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Check if the current authentication is not anonymous (i.e., the user is logged in)
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            // Get the JWT authentication token from the SecurityContext
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            // Synchronize user data with the IDP using the JWT token
            userSynchronizer.synchronizeWithIdp(token.getToken());
        }

        // Continue processing the filter chain
        filterChain.doFilter(request, response);
    }
}
