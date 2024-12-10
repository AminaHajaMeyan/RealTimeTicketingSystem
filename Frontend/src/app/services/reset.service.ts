import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ResetService {
  private resetSource = new Subject<void>();
  reset$ = this.resetSource.asObservable();

  triggerReset(): void {
    this.resetSource.next();
  }
}
