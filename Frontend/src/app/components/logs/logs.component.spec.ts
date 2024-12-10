import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LogsComponent } from './logs.component';
import { TicketService } from '../../services/ticket.service';

describe('LogsComponent', () => {
  let component: LogsComponent;
  let fixture: ComponentFixture<LogsComponent>;
  let mockTicketService: jasmine.SpyObj<TicketService>;

  beforeEach(async () => {
    mockTicketService = jasmine.createSpyObj('TicketService', ['connectToWebSocket']);
    mockTicketService.connectToWebSocket.and.returnValue({
      onmessage: null,
      close: jasmine.createSpy('close'),
    } as unknown as WebSocket);

    await TestBed.configureTestingModule({
      declarations: [LogsComponent],
      providers: [{ provide: TicketService, useValue: mockTicketService }],
    }).compileComponents();

    fixture = TestBed.createComponent(LogsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should establish WebSocket connection on init', () => {
    expect(mockTicketService.connectToWebSocket).toHaveBeenCalled();
  });

  it('should close WebSocket on destroy', () => {
    component.ngOnDestroy();
    expect(mockTicketService.connectToWebSocket().close).toHaveBeenCalled();
  });
});
