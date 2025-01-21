import { Routes } from '@angular/router'; // Importing Angular's routing module
import { MainComponent } from './pages/main/main.component'; // Importing the main component to be used in the route

// Defining the application's routes
export const routes: Routes = [
  {
    path: '', // Default route (empty path)
    component: MainComponent // Component to be loaded for this route
  }
];
