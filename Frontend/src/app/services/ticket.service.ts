import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Configuration {
  totalTickets: number | null;
  maxTicketCapacity: number | null;
  ticketReleaseRate: number | null;
  customerRetrievalRate: number | null;
}

export interface ActivityLog {
  type: string; // INFO, ERROR, UPDATE
  message: string;
  data?: any; // Optional for additional information
}

@Injectable({
  providedIn: 'root',
})
export class TicketService {
  private baseUrl = 'http://localhost:8080/ticket-system';

  constructor(private http: HttpClient) {}


  configureSystem(config: Configuration): Observable<any> {
    return this.http.post(`${this.baseUrl}/configure`, config, { responseType: 'text' });
  }


  startSystem(): Observable<any> {
    return this.http.post(`${this.baseUrl}/start`, {}, { responseType: 'text' });
  }

  stopSystem(): Observable<any> {
    return this.http.post(`${this.baseUrl}/stop`, {});
  }

  getSystemStatus(): Observable<string> {
    return this.http.get<string>(`${this.baseUrl}/status`);
  }

  connectToWebSocket(): WebSocket {
    return new WebSocket('ws://localhost:8080/live-updates');
  }
}
