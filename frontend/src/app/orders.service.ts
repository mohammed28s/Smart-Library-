import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LibraryOrder } from './api.models';

@Injectable({ providedIn: 'root' })
export class OrdersService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getOrders(): Observable<LibraryOrder[]> {
    return this.http.get<LibraryOrder[]>(`${this.apiBaseUrl}/api/orders`);
  }

  createOrder(payload: LibraryOrder): Observable<LibraryOrder> {
    return this.http.post<LibraryOrder>(`${this.apiBaseUrl}/api/orders`, payload);
  }

  updateOrder(id: number, payload: LibraryOrder): Observable<LibraryOrder> {
    return this.http.put<LibraryOrder>(`${this.apiBaseUrl}/api/orders/${id}`, payload);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBaseUrl}/api/orders/${id}`);
  }
}
