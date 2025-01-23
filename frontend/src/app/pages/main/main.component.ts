import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { ChatResponse } from '../../services/models/chat-response';
import { ChatService, MessageService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';
import { MessageResponse } from '../../services/models/message-response';
import { MessageRequest } from '../../services/models/message-request';
import { Notification } from './models/notification';

import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import SockJS from 'sockjs-client';
import * as Stomp from "stompjs";

@Component({
  selector: 'app-main',
  imports: [
    ChatListComponent, // Chat list component for displaying chats
    DatePipe,          // Used for date formatting
    FormsModule,       // Enables template-driven forms
    PickerComponent    // Emoji picker component
  ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit, OnDestroy, AfterViewChecked {

  // Stores the currently selected chat
  selectedChat: ChatResponse = {};

  // Stores the list of all chats
  chats: Array<ChatResponse> = [];

  // Stores the messages of the currently selected chat
  chatMessages: Array<MessageResponse> = [];

  // WebSocket client instance
  socketClient: any = null;

  // Content of the message being typed by the user
  messageContent: string = '';

  // Tracks whether the emoji picker is visible
  showEmojis = false;

  // Element reference to scrollable div for chat messages
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;

  // Stores the WebSocket subscription
  private notificationSubscription: any;

  constructor(
    private chatService: ChatService,       // Service for managing chats
    private messageService: MessageService, // Service for managing messages
    private keycloakService: KeycloakService // Keycloak service for user authentication
  ) { }

  // Lifecycle hook: called after view has been checked
  ngAfterViewChecked(): void {
    this.scrollToBottom(); // Automatically scroll to the bottom of the chat
  }

  // Lifecycle hook: called when the component is destroyed
  ngOnDestroy(): void {
    // Disconnect WebSocket and clean up subscriptions
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();
      this.socketClient = null;
    }
  }

  // Lifecycle hook: called on component initialization
  ngOnInit(): void {
    this.initWebSocket(); // Initialize WebSocket connection
    this.getAllChats();   // Fetch all chats for the user
  }

  // Triggered when a chat is selected
  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse; // Set the selected chat
    this.getAllChatMessages(chatResponse.id as string); // Fetch messages for the chat
    this.setMessagesToSeen(); // Mark messages as seen
    this.selectedChat.unreadCount = 0; // Reset unread count
  }

  // Determines if a message is sent by the current user
  isSelfMessage(message: MessageResponse): boolean {
    return message.senderId === this.keycloakService.userId;
  }

  // Sends a message
  sendMessage() {
    if (this.messageContent) {
      // Prepare message request object
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        recipientId: this.getReceiverId(),
        content: this.messageContent,
        type: 'TEXT',
      };

      // Save message via service
      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          // Update local chat messages and UI
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            recipientId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString()
          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = ''; // Clear input
          this.showEmojis = false;  // Hide emoji picker
        }
      });
    }
  }

  // Triggered when the user presses a key
  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage(); // Send message on Enter key
    }
  }

  // Adds selected emoji to the message content
  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  // Triggered when the chat window is clicked
  onClick() {
    this.setMessagesToSeen(); // Mark messages as seen
  }

  // Uploads media (images, files, etc.)
  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target); // Extract file from input
    if (file !== null) {
      const reader = new FileReader();
      reader.onload = () => {
        if (reader.result) {
          const mediaLines = reader.result.toString().split(',')[1];

          // Upload media via service
          this.messageService.uploadMedia({
            'chat-id': this.selectedChat.id as string,
            body: { file: file }
          }).subscribe({
            next: () => {
              // Add media message to chat messages
              const message: MessageResponse = {
                senderId: this.getSenderId(),
                recipientId: this.getReceiverId(),
                content: 'Attachment',
                type: 'IMAGE',
                state: 'SENT',
                media: [mediaLines],
                createdAt: new Date().toString()
              };
              this.chatMessages.push(message);
            }
          });
        }
      };
      reader.readAsDataURL(file);
    }
  }

  // Logs out the user
  logout() {
    this.keycloakService.logout();
  }

  // Opens the user profile management page
  userProfile() {
    this.keycloakService.accountManagement();
  }

  // Marks messages in the selected chat as seen
  private setMessagesToSeen() {
    this.messageService.setMessagesToSeen({
      'chat-id': this.selectedChat.id as string
    }).subscribe();
  }

  // Fetches all chats for the user
  private getAllChats() {
    this.chatService.getChatsByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      }
    });
  }

  // Fetches all messages for the selected chat
  private getAllChatMessages(chatId: string) {
    this.messageService.getMessages({ 'chat-id': chatId }).subscribe({
      next: (messages) => {
        this.chatMessages = messages;
      }
    });
  }

  // Initializes the WebSocket connection
  private initWebSocket() {
    if (this.keycloakService.keycloak.tokenParsed?.sub) {
      let ws = new SockJS('http://localhost:8080/ws'); // WebSocket server URL
      this.socketClient = Stomp.over(ws);
      const subUrl = `/user/${this.keycloakService.keycloak.tokenParsed?.sub}/chat`;

      // Connect to WebSocket with token
      this.socketClient.connect(
        { 'Authorization': 'Bearer ' + this.keycloakService.keycloak.token },
        () => {
          // Subscribe to notifications
          this.notificationSubscription = this.socketClient.subscribe(subUrl, (message: any) => {
            const notification: Notification = JSON.parse(message.body);
            this.handleNotification(notification); // Handle notifications
          });
        },
        () => console.error('Error while connecting to webSocket')
      );
    }
  }

  // Handles incoming notifications
  private handleNotification(notification: Notification) {
    if (!notification) return;

    // Handle messages for the selected chat
    if (this.selectedChat && this.selectedChat.id === notification.chatId) {
      switch (notification.type) {
        case 'MESSAGE':
        case 'IMAGE':
          const message: MessageResponse = {
            senderId: notification.senderId,
            recipientId: notification.receiverId,
            content: notification.content,
            type: notification.messageType,
            media: notification.media,
            createdAt: new Date().toString()
          };
          if (notification.type === 'IMAGE') {
            this.selectedChat.lastMessage = 'Attachment';
          } else {
            this.selectedChat.lastMessage = notification.content;
          }
          this.chatMessages.push(message);
          break;
        case 'SEEN':
          this.chatMessages.forEach(m => m.state = 'SEEN');
          break;
      }
    } else {
      // Handle messages for other chats
      const destChat = this.chats.find(c => c.id === notification.chatId);
      if (destChat && notification.type !== 'SEEN') {
        if (notification.type === 'MESSAGE') {
          destChat.lastMessage = notification.content;
        } else if (notification.type === 'IMAGE') {
          destChat.lastMessage = 'Attachment';
        }
        destChat.lastMessageTime = new Date().toString();
        destChat.unreadCount! += 1;
      } else if (notification.type === 'MESSAGE') {
        // Handle new chat creation
        const newChat: ChatResponse = {
          id: notification.chatId,
          senderId: notification.senderId,
          recipientId: notification.receiverId,
          lastMessage: notification.content,
          name: notification.chatName,
          unreadCount: 1,
          lastMessageTime: new Date().toString()
        };
        this.chats.unshift(newChat);
      }
    }
  }

  // Gets the sender ID for the current chat
  private getSenderId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.senderId as string;
    }
    return this.selectedChat.recipientId as string;
  }

  // Gets the receiver ID for the current chat
  private getReceiverId(): string {
    if (this.selectedChat.senderId === this.keycloakService.userId) {
      return this.selectedChat.recipientId as string;
    }
    return this.selectedChat.senderId as string;
  }

  // Scrolls the chat view to the bottom
  private scrollToBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }

  // Extracts a file from the input target
  private extractFileFromTarget(target: EventTarget | null): File | null {
    const htmlInputTarget = target as HTMLInputElement;
    if (target === null || htmlInputTarget.files === null) {
      return null;
    }
    return htmlInputTarget.files[0];
  }
}
