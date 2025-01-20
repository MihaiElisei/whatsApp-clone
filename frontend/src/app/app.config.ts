// Import necessary modules and providers from Angular core and related packages
import { ApplicationConfig, provideAppInitializer, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { inject } from '@angular/core';

import { routes } from './app.routes'; // Import the application's routing configuration
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { keycloakHttpInterceptor } from './utils/http/keycloak-http.interceptor'; // Import Keycloak HTTP interceptor
import { KeycloakService } from './utils/keycloak/keycloak.service'; // Import Keycloak service

// Define the application configuration object
export const appConfig: ApplicationConfig = {
  providers: [
    // Optimize Angular's change detection with zone.js by coalescing events
    provideZoneChangeDetection({ eventCoalescing: true }),

    // Register the application's routing configuration
    provideRouter(routes),

    // Provide the HTTP client with the Keycloak HTTP interceptor
    provideHttpClient(
      withInterceptors([keycloakHttpInterceptor]) // Attach the interceptor to all HTTP requests
    ),

    // Initialize the KeycloakService when the app starts
    provideAppInitializer(() => {
      // Use Angular's dependency injection to get the KeycloakService instance
      const initFn = ((key: KeycloakService) => {
        return () => key.init(); // Call the `init` method to initialize Keycloak
      })(inject(KeycloakService)); // Inject the service instance
      return initFn(); // Return the initialization function for execution
    })
  ]
};
