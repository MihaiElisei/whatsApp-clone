import { Component, OnInit } from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { ChatResponse } from '../../services/models/chat-response';
import { ChatService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-main',
  imports: [
    ChatListComponent // Importing the ChatListComponent to display the list of chats
  ],
  templateUrl: './main.component.html', // Template file for the main component
  styleUrl: './main.component.scss' // Styles specific to this component
})
export class MainComponent implements OnInit {

  // Holds the list of chats retrieved from the server
  chats: Array<ChatResponse> = []

  constructor(
    private chatService: ChatService, // Service for chat-related API calls
    private keycloakService: KeycloakService, // Service for Keycloak-related actions
  ) { }

  // Lifecycle hook that runs when the component is initialized
  ngOnInit(): void {
    this.getAllChats(); // Fetches all chats when the component is loaded
  }

  // Fetches chats from the server for the current user
  private getAllChats() {
    this.chatService.getChatsByReceiver()
      .subscribe({
        next: (res) => {
          this.chats = res; // Assigns the retrieved chats to the `chats` array
        }
      });
  }

  // Logs out the user via the Keycloak service
  logout() {
    this.keycloakService.logout();
  }

  // Opens the user account management page via the Keycloak service
  userProfile() {
    this.keycloakService.accountManagement();
  }
}
