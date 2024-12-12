import { Component, OnInit, OnDestroy } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { ResetService } from '../services/reset.service';
import { TicketService } from '../services/ticket.service';

@Component({
  selector: 'app-analytics-dashboard',
  standalone: true,
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.css']
})
export class AnalyticsDashboardComponent implements OnInit, OnDestroy {
  private socket: WebSocket | null = null;
  chart!: Chart;
  salesData: number[] = [];
  labels: string[] = [];
  private readonly MAX_POINTS = 20; // Limit data points for better visualization

  constructor(
    private resetService: ResetService,
    private ticketService: TicketService
  ) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.initChart();
    this.connectToWebSocket();

    // Subscribe to reset events
    this.resetService.reset$.subscribe(() => {
      this.resetChart();
    });
  }

  ngOnDestroy(): void {
    if (this.socket) {
      this.socket.close();
    }
  }

  private initChart(): void {
    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: this.labels,
        datasets: [{
          label: 'Ticket Sales Over Time',
          data: this.salesData,
          borderColor: 'blue',
          borderWidth: 2,
          tension: 0.3,
        }]
      },
      options: {
        responsive: true,
        animation: {
          duration: 0 // Disable animation for real-time updates
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Time'
            }
          },
          y: {
            title: {
              display: true,
              text: 'Tickets Sold'
            }
          }
        }
      }
    };

    const ctx = document.getElementById('salesChart') as HTMLCanvasElement;
    if (ctx) {
      this.chart = new Chart(ctx, config);
    }
  }

  private connectToWebSocket(): void {
    try {
      this.socket = this.ticketService.connectToWebSocket();

      this.socket.onopen = () => {
        console.log('WebSocket connected for real-time updates');
      };

      this.socket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'sale') {
            this.updateChartData(data.amount, data.timestamp);
          }
        } catch (error) {
          console.error('Error processing message:', error);
        }
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.reconnectWebSocket();
      };

      this.socket.onclose = () => {
        console.log('WebSocket connection closed');
        this.reconnectWebSocket();
      };

    } catch (error) {
      console.error('WebSocket connection error:', error);
      this.reconnectWebSocket();
    }
  }

  private reconnectWebSocket(): void {
    setTimeout(() => {
      if (this.socket?.readyState !== WebSocket.OPEN) {
        this.connectToWebSocket();
      }
    }, 3000);
  }

  private updateChartData(amount: number, timestamp: string): void {
    // Maintain fixed number of points for better real-time visualization
    if (this.salesData.length >= this.MAX_POINTS) {
      this.salesData.shift();
      this.labels.shift();
    }

    this.salesData.push(amount);
    this.labels.push(new Date(timestamp).toLocaleTimeString());

    // Update the chart immediately
    if (this.chart) {
      this.chart.update('none'); // 'none' mode for instant updates
    }
  }

  private resetChart(): void {
    // Clear existing data
    this.salesData = [];
    this.labels = [];

    if (this.chart) {
      this.chart.data.labels = this.labels;
      this.chart.data.datasets[0].data = this.salesData;
      this.chart.update();
    }

    // Reconnect WebSocket
    if (this.socket) {
      this.socket.close();
    }
    this.connectToWebSocket();
  }
}
