import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardTotals {
  totalErrors: number;
  totalDistinctObjects: number;
  totalDistinctPrograms: number;
}

export interface AggregatedMetric {
  label: string;
  value: number;
}

export interface ErrorVariable {
  sequence: number;
  variableName: string;
  variableValue: string;
}

export interface ErrorCorrelation {
  id: string;
  cicsCpu: string;
  taskCode: string;
  creditObjectNumber: string;
  creditObjectType: string;
  createdAt: string;
}

export interface CobolError {
  id: string;
  correlation: ErrorCorrelation;
  errorCode: string;
  errorMessage: string;
  programName: string;
  timestamp: string;
  variables?: ErrorVariable[];
}

export interface ErrorDetailDto {
  error: CobolError;
  correlation: ErrorCorrelation;
  variables: ErrorVariable[];
  relatedErrors: CobolError[];
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8082/api/v1/metrics';

  constructor(private http: HttpClient) {}

  getTotals(): Observable<DashboardTotals> {
    return this.http.get<DashboardTotals>(`${this.baseUrl}/totals`);
  }

  getErrorsByDay(): Observable<AggregatedMetric[]> {
    return this.http.get<AggregatedMetric[]>(`${this.baseUrl}/by-day`);
  }

  getErrorsByHour(): Observable<AggregatedMetric[]> {
    return this.http.get<AggregatedMetric[]>(`${this.baseUrl}/by-hour`);
  }

  getErrors(programName?: string, creditObject?: string): Observable<CobolError[]> {
    let url = `${this.baseUrl}/errors?`;
    if (programName) url += `programName=${encodeURIComponent(programName)}&`;
    if (creditObject) url += `creditObject=${encodeURIComponent(creditObject)}&`;
    return this.http.get<CobolError[]>(url);
  }

  getErrorDetail(id: string): Observable<ErrorDetailDto> {
    return this.http.get<ErrorDetailDto>(`${this.baseUrl}/errors/${id}/detail`);
  }
}
