import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TicketService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  configureSystem(config: any): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/configure`, config);
  }

  startSystem(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/start`, {});
  }

  stopSystem(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/stop`, {});
  }

  resetSystem(): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/reset`, {});
  }

  getActivityStream(): EventSource {
    return new EventSource(`${this.baseUrl}/activity-stream`);
  }
}
