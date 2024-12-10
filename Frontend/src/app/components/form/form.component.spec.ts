import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormComponent } from './form.component';
import { TicketService } from '../../services/ticket.service';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [FormsModule, HttpClientTestingModule],
      providers: [TicketService],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate configuration inputs correctly', () => {
    component.config.totalTickets = 100;
    component.config.maxTicketCapacity = 50;

    expect(component.config.totalTickets).toBeGreaterThanOrEqual(component.config.maxTicketCapacity);
  });

  it('should call configureSystem on button click', () => {
    spyOn(component, 'configureSystem');
    const button = fixture.debugElement.nativeElement.querySelector('button');
    button.click();
    expect(component.configureSystem).toHaveBeenCalled();
  });
});
