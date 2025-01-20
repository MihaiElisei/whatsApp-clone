import {ApplicationConfig, provideAppInitializer, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import { inject } from '@angular/core';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import Keycloak from 'keycloak-js';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideAppInitializer(() => {
      const initFn = ((key:Keycloak) => {
        return () => key.init()
      }) (inject(Keycloak));
      return initFn();
    })
    ]
};
