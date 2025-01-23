import {Component, input, InputSignal, output} from '@angular/core';
import {ChatResponse} from '../../services/models/chat-response';
import {DatePipe} from '@angular/common';
import {UserResponse} from '../../services/models/user-response';
import {UserService} from '../../services/services/user.service';
import {ChatService} from '../../services/services/chat.service';
import {KeycloakService} from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat-list', // Specifies the selector used to include this component in a template.
  templateUrl: './chat-list.component.html', // Path to the HTML template for this component.
  imports: [
    DatePipe // Import Angular's DatePipe for formatting dates.
  ],
  styleUrl: './chat-list.component.scss' // Path to the SCSS styles for this component.
})
export class ChatListComponent {
  // InputSignal to receive the list of chat objects as input.
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);

  // Flag to indicate whether the user is searching for a new contact.
  searchNewContact = false;

  // Array to hold the list of all users (contacts).
  contacts: Array<UserResponse> = [];

  // Output signal to emit the selected chat when a user selects or clicks on a chat.
  chatSelected = output<ChatResponse>();

  // Constructor to inject required services.
  constructor(
    private chatService: ChatService, // Service to handle chat-related API calls.
    private userService: UserService, // Service to handle user-related API calls.
    private keycloakService: KeycloakService // Service for Keycloak authentication and user information.
  ) {}

  /**
   * Retrieves all users (contacts) from the server to display for new chat creation.
   */
  searchContact() {
    this.userService.getAllUsers() // Call the user service to fetch all users.
      .subscribe({
        next: (users) => {
          this.contacts = users; // Assign fetched users to the `contacts` array.
          this.searchNewContact = true; // Show the contact search UI.
        }
      });
  }

  /**
   * Handles the selection of a contact and creates a new chat between the current user and the selected contact.
   *
   * @param contact - The selected contact object.
   */
  selectContact(contact: UserResponse) {
    // Call the chat service to create a new chat between the current user and the selected contact.
    this.chatService.createChat({
      'sender-id': this.keycloakService.userId as string, // Current user ID.
      'recipient-id': contact.id as string // Selected contact's user ID.
    }).subscribe({
      next: (res) => {
        // Create a ChatResponse object from the response and the selected contact's details.
        const chat: ChatResponse = {
          id: res.response, // ID of the created chat.
          name: contact.firstName + ' ' + contact.lastName, // Name of the contact.
          recipientOnline: contact.online, // Online status of the contact.
          lastMessageTime: contact.lastSeen, // Last seen time of the contact.
          senderId: this.keycloakService.userId, // Current user's ID.
          recipientId: contact.id // Selected contact's ID.
        };

        // Add the new chat to the top of the chat list.
        this.chats().unshift(chat);

        // Hide the contact search UI after creating the chat.
        this.searchNewContact = false;

        // Emit the newly created chat to notify other components.
        this.chatSelected.emit(chat);
      }
    });
  }

  /**
   * Handles a click on a chat in the chat list.
   *
   * @param chat - The clicked chat object.
   */
  chatClicked(chat: ChatResponse) {
    // Emit the clicked chat to notify other components or services.
    this.chatSelected.emit(chat);
  }

  /**
   * Formats the last message text to ensure it fits within a defined length.
   *
   * @param lastMessage - The last message text to format.
   * @returns A string representing the formatted last message.
   */
  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      // If the message is short enough, return it as is.
      return lastMessage;
    }
    // Truncate messages longer than 20 characters and add '...' at the end.
    return lastMessage?.substring(0, 17) + '...';
  }
}
