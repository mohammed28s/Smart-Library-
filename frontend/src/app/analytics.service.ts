import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InventoryMetrics, RevenueMetrics } from './api.models';

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getInventoryMetrics(): Observable<InventoryMetrics> {
    return this.http.get<InventoryMetrics>(`${this.apiBaseUrl}/api/analytics/inventory`);
  }

  getRevenueMetrics(): Observable<RevenueMetrics> {
    return this.http.get<RevenueMetrics>(`${this.apiBaseUrl}/api/analytics/revenue`);
  }
}
