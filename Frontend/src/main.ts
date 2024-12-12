import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';
import {TicketProgressBarComponent} from './app/ticket-progress-bar/ticket-progress-bar.component';


bootstrapApplication(AppComponent, {
  providers: [provideHttpClient()],
}).catch((err) => console.error(err));


bootstrapApplication(TicketProgressBarComponent)
  .catch(err => console.error(err));
