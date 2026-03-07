import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Payment } from './api.models';

@Injectable({ providedIn: 'root' })
export class PaymentsService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiBaseUrl}/api/payments`);
  }

  createPayment(payload: Payment): Observable<Payment> {
    return this.http.post<Payment>(`${this.apiBaseUrl}/api/payments`, payload);
  }

  updatePayment(id: number, payload: Payment): Observable<Payment> {
    return this.http.put<Payment>(`${this.apiBaseUrl}/api/payments/${id}`, payload);
  }

  deletePayment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBaseUrl}/api/payments/${id}`);
  }
}
