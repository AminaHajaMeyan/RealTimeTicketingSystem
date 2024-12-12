import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-ticket-progress-bar',
  standalone: true,
  templateUrl: './ticket-progress-bar.component.html',
  styleUrls: ['./ticket-progress-bar.component.css']
})
export class TicketProgressBarComponent implements OnInit, OnDestroy {
  private socket: WebSocket | null = null;
  status = {
    totalTickets: 0,
    ticketsSold: 0,
    remainingTickets: 0
  };

  constructor() {}

  ngOnInit(): void {
    this.connectToWebSocket();
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }

  connectToWebSocket(): void {
    this.socket = new WebSocket('ws://localhost:8080/live-updates');

    this.socket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      this.status = data;
      console.log('Received WebSocket Data:', data); // Debug log
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket Error:', error);
    };

    this.socket.onclose = () => {
      console.log('WebSocket connection closed.');
    };
  }

  get progress(): number {
    return this.status.totalTickets
      ? (this.status.ticketsSold / this.status.totalTickets) * 100
      : 0;
  }
}
