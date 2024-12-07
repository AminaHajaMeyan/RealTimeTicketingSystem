import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent {
  config = {
    totalTickets: 100,
    maxCapacity: 10,
    releaseRate: 2,
    retrievalRate: 1
  };

  constructor(private ticketService: TicketService) {}

  onSubmit() {
    this.ticketService.configureSystem(this.config).subscribe({
      next: () => alert('System configured successfully!'),
      error: (err) => alert('Failed to configure system: ' + err.message)
    });
  }

  resetForm() {
    this.config = {
      totalTickets: 100,
      maxCapacity: 10,
      releaseRate: 2,
      retrievalRate: 1
    };
  }

  startSystem() {
    this.ticketService.startSystem().subscribe(() => alert('System started.'));
  }

  stopSystem() {
    this.ticketService.stopSystem().subscribe(() => alert('System stopped.'));
  }

  resetSystem() {
    this.ticketService.resetSystem().subscribe(() => alert('System reset.'));
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
