import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserDto } from './api.models';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getUsers(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(`${this.apiBaseUrl}/api/users`);
  }

  createUser(payload: UserDto): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.apiBaseUrl}/api/users`, payload);
  }

  updateUser(id: number, payload: UserDto): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.apiBaseUrl}/api/users/${id}`, payload);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBaseUrl}/api/users/${id}`);
  }
}
