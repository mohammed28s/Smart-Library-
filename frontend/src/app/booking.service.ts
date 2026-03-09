import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Appointment, ReadingRoom } from './api.models';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getRooms(): Observable<ReadingRoom[]> {
    return this.http.get<ReadingRoom[]>(`${this.apiBaseUrl}/api/booking/rooms`);
  }

  getAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiBaseUrl}/api/booking/appointments`);
  }

  createAppointment(payload: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.apiBaseUrl}/api/booking/appointments`, payload);
  }
}
