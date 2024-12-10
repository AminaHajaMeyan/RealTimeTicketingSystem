import { Component, OnInit, OnDestroy } from '@angular/core';
import { TicketService, ActivityLog } from '../../services/ticket.service';

import { CommonModule } from '@angular/common';@Component({
  selector: 'app-live-activity',
  templateUrl: './live-activity.component.html',
  styleUrls: ['./live-activity.component.css'],
  standalone: true,
  imports: [CommonModule], // Include CommonModule
})

export class LiveActivityComponent implements OnInit, OnDestroy {
  logs: ActivityLog[] = [];
  private socket: WebSocket | null = null;

  constructor(private ticketService: TicketService) {}

  ngOnInit(): void {
    this.socket = this.ticketService.connectToWebSocket();
    this.socket.onmessage = (event) => {
      this.logs.push(event.data);
    };
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }
}
