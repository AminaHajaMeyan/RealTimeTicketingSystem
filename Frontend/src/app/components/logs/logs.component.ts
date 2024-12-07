import { Component, OnInit, OnDestroy } from '@angular/core';
import { TicketService } from '../../services/ticket.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [CommonModule], // Import CommonModule for *ngFor
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
        this.logs.push(event.data);
      };
    }
  }

  ngOnDestroy() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
