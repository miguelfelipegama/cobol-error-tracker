import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, CobolError, DashboardTotals, AggregatedMetric, ErrorDetailDto } from './services/api.service';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, AfterViewInit {
  totals: DashboardTotals = { totalErrors: 0, totalDistinctObjects: 0, totalDistinctPrograms: 0 };
  errors: CobolError[] = [];
  loading = false;

  filterProgram = '';
  filterObject = '';

  // Detail drawer
  selectedDetail: ErrorDetailDto | null = null;
  drawerOpen = false;
  detailLoading = false;

  @ViewChild('hourChart') hourChartRef!: ElementRef;
  @ViewChild('dayChart') dayChartRef!: ElementRef;

  private hourChartInst: Chart | null = null;
  private dayChartInst: Chart | null = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadData();
  }

  ngAfterViewInit() {
    this.initCharts();
    this.loadChartData();
  }

  loadData() {
    this.loading = true;
    this.api.getTotals().subscribe(res => this.totals = res);
    this.api.getErrors().subscribe(res => {
      this.errors = res;
      this.loading = false;
    });
  }

  loadChartData() {
    this.api.getErrorsByHour().subscribe(res => {
      this.updateChart(this.hourChartInst, res, 'Erros por Hora', '#f43f5e');
    });
    this.api.getErrorsByDay().subscribe(res => {
      this.updateChart(this.dayChartInst, res, 'Erros por Dia', '#3b82f6');
    });
  }

  applyFilters() {
    this.loading = true;
    this.api.getErrors(this.filterProgram, this.filterObject).subscribe(res => {
      this.errors = res;
      this.loading = false;
    });
  }

  clearFilters() {
    this.filterProgram = '';
    this.filterObject = '';
    this.loadData();
  }

  openDetail(err: CobolError) {
    if (this.selectedDetail?.error?.id === err.id) return;
    this.drawerOpen = true;
    this.detailLoading = true;
    this.selectedDetail = null;
    this.api.getErrorDetail(err.id).subscribe(detail => {
      this.selectedDetail = detail;
      this.detailLoading = false;
    });
  }

  closeDrawer() {
    this.drawerOpen = false;
    this.selectedDetail = null;
  }

  isCurrentError(id: string): boolean {
    return this.selectedDetail?.error?.id === id;
  }

  initCharts() {
    const chartDefaults = {
      plugins: {
        legend: { display: false }
      },
      scales: {
        x: {
          ticks: { color: '#94a3b8' },
          grid: { color: '#1e293b' }
        },
        y: {
          ticks: { color: '#94a3b8' },
          grid: { color: '#334155' },
          beginAtZero: true
        }
      }
    };

    if (this.hourChartRef) {
      this.hourChartInst = new Chart(this.hourChartRef.nativeElement, {
        type: 'bar',
        data: { labels: [], datasets: [] },
        options: { responsive: true, ...chartDefaults }
      });
    }
    if (this.dayChartRef) {
      this.dayChartInst = new Chart(this.dayChartRef.nativeElement, {
        type: 'line',
        data: { labels: [], datasets: [] },
        options: { responsive: true, ...chartDefaults }
      });
    }
  }

  updateChart(chart: Chart | null, data: AggregatedMetric[], label: string, color: string) {
    if (!chart) return;
    chart.data.labels = data.map(d => d.label);
    chart.data.datasets = [{
      label,
      data: data.map(d => d.value),
      backgroundColor: color + '99',
      borderColor: color,
      borderWidth: 2,
      tension: 0.4,
      pointBackgroundColor: color,
      fill: true
    }];
    chart.update();
  }
}
