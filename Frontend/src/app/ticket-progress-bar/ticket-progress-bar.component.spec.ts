import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketProgressBarComponent } from './ticket-progress-bar.component';

describe('TicketProgressBarComponent', () => {
  let component: TicketProgressBarComponent;
  let fixture: ComponentFixture<TicketProgressBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketProgressBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketProgressBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
