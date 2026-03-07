import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  userId: number;
  username: string;
  email: string | null;
  fullName: string | null;
  role: 'USER' | 'WORKER' | 'GUEST';
}

export interface ForgotPasswordResponse {
  message: string;
  resetToken?: string;
}

export interface ForgotPasswordSmsResponse {
  message: string;
  resetCode?: string;
}

export interface AuthMessageResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiBaseUrl = '';
  private readonly tokenKey = 'smartlibrary_token';
  private readonly usernameKey = 'smartlibrary_username';
  private readonly roleKey = 'smartlibrary_role';
  private readonly guestKey = 'smartlibrary_guest';

  private readonly tokenSubject = new BehaviorSubject<string>(localStorage.getItem(this.tokenKey) ?? '');
  readonly token$ = this.tokenSubject.asObservable();

  constructor(private readonly http: HttpClient) {}

  get token(): string {
    return this.tokenSubject.value;
  }

  get username(): string {
    return localStorage.getItem(this.usernameKey) ?? '';
  }

  get role(): string {
    return localStorage.getItem(this.roleKey) ?? '';
  }

  get isGuest(): boolean {
    return localStorage.getItem(this.guestKey) === '1';
  }

  get isAuthenticated(): boolean {
    return this.token.length > 0;
  }

  get isSessionActive(): boolean {
    return this.isAuthenticated || this.isGuest;
  }

  register(payload: {
    username: string;
    email: string;
    phone?: string | null;
    password: string;
    fullName?: string | null;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiBaseUrl}/api/auth/register`, payload)
      .pipe(tap((response) => this.storeAuth(response)));
  }

  login(payload: { username: string; password: string }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiBaseUrl}/api/auth/login`, payload)
      .pipe(tap((response) => this.storeAuth(response)));
  }

  forgotPassword(email: string): Observable<ForgotPasswordResponse> {
    return this.http.post<ForgotPasswordResponse>(`${this.apiBaseUrl}/api/auth/forgot-password`, { email });
  }

  forgotPasswordSms(phone: string): Observable<ForgotPasswordSmsResponse> {
    return this.http.post<ForgotPasswordSmsResponse>(`${this.apiBaseUrl}/api/auth/forgot-password-sms`, { phone });
  }

  resetPassword(token: string, newPassword: string): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiBaseUrl}/api/auth/reset-password`, {
      token,
      newPassword
    });
  }

  resetPasswordSms(phone: string, code: string, newPassword: string): Observable<AuthMessageResponse> {
    return this.http.post<AuthMessageResponse>(`${this.apiBaseUrl}/api/auth/reset-password-sms`, {
      phone,
      code,
      newPassword
    });
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usernameKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.guestKey);
    this.tokenSubject.next('');
  }

  continueAsGuest(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.setItem(this.usernameKey, 'Guest');
    localStorage.setItem(this.roleKey, 'GUEST');
    localStorage.setItem(this.guestKey, '1');
    this.tokenSubject.next('');
  }

  private storeAuth(response: AuthResponse): void {
    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.usernameKey, response.username);
    localStorage.setItem(this.roleKey, response.role);
    localStorage.removeItem(this.guestKey);
    this.tokenSubject.next(response.token);
  }
}
