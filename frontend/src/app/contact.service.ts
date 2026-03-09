import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ContactRequest, ContactResponse } from './api.models';

@Injectable({ providedIn: 'root' })
export class ContactService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  sendMessage(payload: ContactRequest): Observable<ContactResponse> {
    return this.http.post<ContactResponse>(`${this.apiBaseUrl}/api/contact`, payload);
  }
}
