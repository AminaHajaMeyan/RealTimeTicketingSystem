import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [CommonModule], // Import CommonModule here
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent implements OnInit, OnDestroy {
  logs: string[] = [];
  private eventSource: EventSource | null = null;

  constructor(private ticketService: TicketService) {}

  ngOnInit() {
    this.eventSource = this.ticketService.getActivityStream();
    if (this.eventSource) {
      this.eventSource.onmessage = (event) => {
        this.logs.push(event.data); // Push new log entries
      };
      this.eventSource.onerror = () => {
        alert('Connection lost. Reconnecting...');
        this.eventSource?.close();
        setTimeout(() => this.ngOnInit(), 5000); // Attempt to reconnect after 5 seconds
      };
    }
  }

  ngOnDestroy() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
