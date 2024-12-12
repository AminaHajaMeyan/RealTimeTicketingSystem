import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { FormComponent } from './components/form/form.component';
import { LogsComponent } from './components/logs/logs.component';
import { HttpClientModule } from '@angular/common/http';
import {TicketProgressBarComponent} from './ticket-progress-bar/ticket-progress-bar.component';
import {AnalyticsDashboardComponent} from './analytics-dashboard/analytics-dashboard.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, FormComponent, LogsComponent, HttpClientModule, TicketProgressBarComponent, AnalyticsDashboardComponent], // Import modules
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title: string = 'Ticketing System'; // App title
}
