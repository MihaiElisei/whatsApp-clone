// Import necessary modules and services from Angular and Keycloak
import { HttpHeaders, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { KeycloakService } from '../keycloak/keycloak.service';

// Define the Keycloak HTTP interceptor function
export const keycloakHttpInterceptor: HttpInterceptorFn = (req, next) => {
  // Use Angular's dependency injection to get an instance of the KeycloakService
  const keycloakService = inject(KeycloakService);

  // Retrieve the Keycloak token from the service
  const token = keycloakService.keycloak.token;

  // If a token is available, clone the request and add the Authorization header
  if (token) {
    const authReq = req.clone({
      headers: new HttpHeaders({
        // Attach the Bearer token for authorization
        Authorization: `Bearer ${token}`
      })
    });

    // Pass the modified request to the next handler in the chain
    return next(authReq);
  }

  // If no token is available, pass the original request to the next handler
  return next(req);
};
