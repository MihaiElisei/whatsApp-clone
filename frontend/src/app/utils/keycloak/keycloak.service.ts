// Import required modules from Angular and Keycloak
import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { Router } from '@angular/router';

// Mark this service as injectable and available throughout the app
@Injectable({
  providedIn: 'root' // Ensures the service is a singleton
})
export class KeycloakService {

  // Declare a private property to hold the Keycloak instance
  private _keycloak: Keycloak | undefined;

  // Inject Angular's Router for navigation purposes
  constructor(private router: Router) {}

  // Getter for the Keycloak instance
  get keycloak() {
    // Lazily initialize the Keycloak instance if it hasn't been created yet
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090', // Keycloak server URL
        realm: 'whatsapp-clone',      // The realm configured in Keycloak
        clientId: 'whatsapp-clone-app' // Client ID registered in the realm
      });
    }
    return this._keycloak;
  }

  // Initialize Keycloak
  async init() {
    const authenticated = await this.keycloak.init({
      onLoad: 'login-required', // Automatically redirects to login if not authenticated
      // silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
      // Uncomment and configure for silent SSO
      // checkLoginIframe: false // Disable iframe-based login checks (optional)
    });

    // Optional: Handle authentication status or redirection
    if (!authenticated) {
      console.warn('User is not authenticated.');
    }
  }

  // Trigger the login process
  async login() {
    await this.keycloak.login(); // Redirects the user to the Keycloak login page
  }

  // Get the user's unique ID from the Keycloak token
  get userId(): string {
    return this.keycloak?.tokenParsed?.sub as string; // `sub` represents the user ID
  }

  // Check if the Keycloak token is valid
  get isTokenValid(): boolean {
    return !this.keycloak.isTokenExpired(); // Returns true if the token has not expired
  }

  // Get the full name of the user from the Keycloak token
  get fullName(): string {
    return this.keycloak.tokenParsed?.['name'] as string; // Extracts `name` from the parsed token
  }

  // Log out the user and redirect to the specified URL
  logout() {
    return this.keycloak.logout({
      redirectUri: 'http://localhost:4200' // URL to navigate after logout
    });
  }

  // Redirect the user to the Keycloak account management page
  accountManagement() {
    return this.keycloak.accountManagement(); // Opens the account management page
  }
}
