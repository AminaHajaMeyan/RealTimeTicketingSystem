import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Configuration, TicketService } from '../../services/ticket.service';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from '../header/header.component';
import { ResetService } from '../../services/reset.service';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
})
export class FormComponent {
  config: Configuration = {
    totalTickets: null,
    maxTicketCapacity: null,
    ticketReleaseRate: null,
    customerRetrievalRate: null,
  };


  logs: string[] = [];

  isLoading = false; // For showing loading state

  constructor(private ticketService: TicketService, private resetService: ResetService) {}

  private validateConfig(): boolean {
    if (
      !this.config.totalTickets ||
      !this.config.maxTicketCapacity ||
      !this.config.ticketReleaseRate ||
      !this.config.customerRetrievalRate
    ) {
      alert('All fields are required. Please fill in all values.');
      return false;
    }

    if (this.config.totalTickets < this.config.maxTicketCapacity) {
      alert('Total Tickets must be greater than or equal to Max Ticket Capacity.');
      return false;
    }

    if (this.config.ticketReleaseRate > this.config.maxTicketCapacity) {
      alert('Ticket Release Rate must not exceed Max Ticket Capacity.');
      return false;
    }

    if (this.config.customerRetrievalRate > this.config.maxTicketCapacity) {
      alert('Customer Retrieval Rate must not exceed Max Ticket Capacity.');
      return false;
    }

    return true;
  }

  configureSystem(): void {
    if (!this.validateConfig()) return;

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
    if (!this.validateConfig()) return;

    this.ticketService.startSystem().subscribe({
      next: () => alert('System started successfully!'),
      error: (error) => alert(`Failed to start system: ${error.message}`),
    });

  }



  stopSystem(): void {
    this.isLoading = true;
    this.ticketService.stopSystem().subscribe({
      next: () => {
        alert('System stopped successfully!');
        this.isLoading = false;
      },
      error: (error) => {
        alert(`Failed to stop system: ${error.message}`);
        this.isLoading = false;
      },
    });
  }

  resetSystem(): void {
    this.ticketService.resetSystem().subscribe({
      next: () => {
        this.config = {
          totalTickets: null,
          maxTicketCapacity: null,
          ticketReleaseRate: null,
          customerRetrievalRate: null
        };
        this.resetService.triggerReset(); // Notify other components about the reset
        alert('System reset successfully!');
      },
      error: (error) => {
        alert(`Failed to reset system: ${error.message}`);
      }
    });
  }

}

