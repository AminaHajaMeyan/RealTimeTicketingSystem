import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root' // Ensures it is available globally
})
export class TicketService {
  private baseUrl = 'http://localhost:8080/api'; // Backend API base URL

  constructor(private http: HttpClient) {}

  configureSystem(config: any) {
    return this.http.post(`${this.baseUrl}/configure`, config);
  }

  startSystem() {
    return this.http.post(`${this.baseUrl}/start`, {});
  }

  stopSystem() {
    return this.http.post(`${this.baseUrl}/stop`, {});
  }

  resetSystem() {
    return this.http.post(`${this.baseUrl}/reset`, {});
  }

  getActivityStream(): EventSource {
    return new EventSource(`${this.baseUrl}/activity-stream`);
  }
}
