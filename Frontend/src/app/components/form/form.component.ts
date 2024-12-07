import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css'],
})
export class FormComponent {
  config = {
    totalTickets: 100,
    maxCapacity: 10,
    releaseRate: 2,
    retrievalRate: 1,
  };

  constructor(private ticketService: TicketService) {}

  onSubmit() {
    if (this.formValid()) {
      this.ticketService.configureSystem(this.config).subscribe({
        next: () => alert('System configured successfully!'),
        error: (err) => alert('Failed to configure system: ' + err.message),
      });
    } else {
      alert('Please correct the form errors before submitting.');
    }
  }

  startSystem() {
    this.ticketService.startSystem().subscribe({
      next: () => alert('System started.'),
      error: (err) => alert('Failed to start system: ' + err.message),
    });
  }

  stopSystem() {
    this.ticketService.stopSystem().subscribe({
      next: () => alert('System stopped.'),
      error: (err) => alert('Failed to stop system: ' + err.message),
    });
  }

  resetSystem() {
    this.ticketService.resetSystem().subscribe({
      next: () => alert('System reset.'),
      error: (err) => alert('Failed to reset system: ' + err.message),
    });
  }

  formValid() {
    return (
      this.config.totalTickets > 0 &&
      this.config.maxCapacity > 0 &&
      this.config.maxCapacity <= this.config.totalTickets &&
      this.config.releaseRate > 0 &&
      this.config.retrievalRate > 0
    );
  }
}
