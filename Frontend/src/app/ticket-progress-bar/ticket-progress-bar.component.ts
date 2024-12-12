import { Component, OnInit, OnDestroy } from '@angular/core';
import { ResetService } from '../services/reset.service';

@Component({
  selector: 'app-ticket-progress-bar',
  standalone: true,
  templateUrl: './ticket-progress-bar.component.html',
  styleUrls: ['./ticket-progress-bar.component.css']
})
export class TicketProgressBarComponent implements OnInit, OnDestroy {
  private socket: WebSocket | null = null;

  // Define the status object
  status = {
    totalTickets: 0,
    ticketsSold: 0,
    remainingTickets: 0
  };

  constructor(private resetService: ResetService) {}

  ngOnInit(): void {
    this.connectToWebSocket();

    // Subscribe to reset events
    this.resetService.reset$.subscribe(() => {
      this.resetProgressBar();
    });
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }

  connectToWebSocket(): void {
    this.socket = new WebSocket('ws://localhost:8080/live-updates');

    this.socket.onmessage = (event) => {
      const message = event.data;

      try {
        const data = JSON.parse(message);

        // Process only pool status messages
        if (data.totalTickets !== undefined) {
          this.status = {
            totalTickets: data.totalTickets,
            ticketsSold: data.ticketsSold,
            remainingTickets: data.remainingTickets,
          };
        }
      } catch (e) {
        console.error('Error parsing WebSocket message:', message, e);
      }
    };
  }



  resetProgressBar(): void {
    console.log('Progress bar reset.');
    this.status = {
      totalTickets: 0,
      ticketsSold: 0,
      remainingTickets: 0
    };

    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }

    setTimeout(() => {
      this.connectToWebSocket();
    }, 500);
  }

  // Define the progress getter
  get progress(): number {
    return this.status.totalTickets
      ? (this.status.ticketsSold / this.status.totalTickets) * 100
      : 0;
  }
}
