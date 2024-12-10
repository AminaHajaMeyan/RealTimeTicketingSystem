import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {Configuration, TicketService} from '../../services/ticket.service';
import { HttpClientModule } from '@angular/common/http';


@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule], // Add HttpClientModule here
})

export class FormComponent {
  config = {
    totalTickets: null,
    maxTicketCapacity: null,
    ticketReleaseRate: null,
    customerRetrievalRate: null,
  };

  isLoading = false; // For showing loading state

  constructor(private ticketService: TicketService) {}

  configureSystem(): void {
    if (this.config.totalTickets && this.config.maxTicketCapacity) {
      if (this.config.totalTickets <= this.config.maxTicketCapacity) {
        alert('Total Tickets must be greater than or equal to Max Ticket Capacity.');
        return;
      }
    }

    this.isLoading = true;
    this.ticketService.configureSystem(this.config).subscribe({
      next: () => {
        alert('System configured successfully!');
        this.isLoading = false;
      },
      error: (error) => {
        alert(`Configuration failed: ${error.message}`);
        this.isLoading = false;
      },
    });
  }

  startSystem(): void {
    this.ticketService.startSystem().subscribe({
      next: () => alert('System started successfully!'),
      error: (error) => alert(`Failed to start system: ${error.message}`),
    });
  }

  stopSystem(): void {
    this.ticketService.stopSystem().subscribe({
      next: () => alert('System stopped successfully!'),
      error: (error) => alert(`Failed to stop system: ${error.message}`),
    });
  }
}
