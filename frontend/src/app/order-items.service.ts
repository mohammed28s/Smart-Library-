import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderItem } from './api.models';

@Injectable({ providedIn: 'root' })
export class OrderItemsService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getOrderItems(): Observable<OrderItem[]> {
    return this.http.get<OrderItem[]>(`${this.apiBaseUrl}/api/order-items`);
  }

  createOrderItem(payload: OrderItem): Observable<OrderItem> {
    return this.http.post<OrderItem>(`${this.apiBaseUrl}/api/order-items`, payload);
  }

  updateOrderItem(id: number, payload: OrderItem): Observable<OrderItem> {
    return this.http.put<OrderItem>(`${this.apiBaseUrl}/api/order-items/${id}`, payload);
  }

  deleteOrderItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBaseUrl}/api/order-items/${id}`);
  }
}
