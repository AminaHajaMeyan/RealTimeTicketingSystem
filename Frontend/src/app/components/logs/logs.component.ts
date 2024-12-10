import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../services/ticket.service';

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

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.socket = this.ticketService.connectToWebSocket();

    this.socket.onmessage = (event) => {
      const message = event.data;
      this.liveUpdates.push(message);

      // Optional: Limit the number of updates displayed
      if (this.liveUpdates.length > 1000) {
        this.liveUpdates.shift();
      }
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }
}
