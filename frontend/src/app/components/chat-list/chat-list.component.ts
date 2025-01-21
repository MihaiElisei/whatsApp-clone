import {Component, input, InputSignal} from '@angular/core';
import {ChatResponse} from '../../services/models/chat-response';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {UserResponse} from '../../services/models/user-response';
import {UserService} from '../../services/services/user.service';

@Component({
  selector: 'app-chat-list', // Defines the component's selector for usage in templates
  imports: [
    NgOptimizedImage, // Optimized image handling for Angular
    DatePipe // Provides date formatting functionality
  ],
  templateUrl: './chat-list.component.html', // Specifies the component's HTML template
  styleUrl: './chat-list.component.scss' // Specifies the component's SCSS stylesheet
})
export class ChatListComponent {

  // Input property to receive chat data as an observable signal
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = false; // Tracks whether the user is searching for a new contact
  contacts: Array<UserResponse> = []; // Stores a list of contacts for search functionality

  constructor(private userService: UserService) { // Injects the UserService for fetching data
  }

  // Initiates a search for all users
  searchContact() {
    this.userService.getAllUsers()
      .subscribe({
        next: users => {
          this.contacts = users; // Updates the contacts list with fetched users
          this.searchNewContact = true; // Sets the search mode to true
        }
      });
  }

  // Handles the event when a chat is clicked (currently empty)
  chatClicked(chat: ChatResponse) {
    // TODO: Implement logic for handling chat click
  }

  // Shortens the last message if it's longer than 20 characters
  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage; // Return the message as is if it's short enough
    }
    return lastMessage?.substring(0, 17) + '...'; // Truncates the message and appends '...'
  }

  // Handles the event when a contact is selected (currently empty)
  selectContact(contact: UserResponse) {
    // TODO: Implement logic for selecting a contact
  }
}
