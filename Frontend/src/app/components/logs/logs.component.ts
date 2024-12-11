import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../services/ticket.service';
import { ResetService } from '../../services/reset.service';

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css'],
  standalone: true,
  imports: [CommonModule], // Import CommonModule to use *ngFor and *ngIf
})
export class LogsComponent implements OnInit, OnDestroy {
  logs: string[] = [];
  liveUpdates: string[] = [];
  private socket: WebSocket | null = null;

  constructor(private ticketService: TicketService, private resetService: ResetService) {}

  ngOnInit(): void {
    this.initializeWebSocket();

    // Listen for reset events
    this.resetService.reset$.subscribe(() => {
      this.clearLiveUpdates();
      console.log('Reset event received. Live updates cleared.');
    });
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }

  private initializeWebSocket(): void {
    this.socket = this.ticketService.connectToWebSocket();

    this.socket.onmessage = (event) => {
      const message = event.data;
      console.log('WebSocket Message:', message); // Log WebSocket messages
      if (message.includes('[System] Reset triggered.')) {
        this.clearLiveUpdates(); // Handle reset event
      } else {
        this.liveUpdates.push(message);
      }

      // Optional: Limit the number of updates displayed
      if (this.liveUpdates.length > 1000) {
        this.liveUpdates.shift();
      }
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  clearLiveUpdates(): void {
    this.liveUpdates = [];
    if (this.socket) {
      this.socket.close();
    }

    // Reconnect to WebSocket after reset
    this.initializeWebSocket();

    alert('Live updates cleared and WebSocket reconnected!');
  }
}
