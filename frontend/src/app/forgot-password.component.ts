import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService, ForgotPasswordResponse, ForgotPasswordSmsResponse } from './auth.service';
import { ToastService } from './toast.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
  email = '';
  phone = '';
  resetToken = '';
  resetCode = '';
  newPassword = '';
  mode: 'email' | 'sms' = 'email';
  loading = false;
  resetting = false;
  message = '';
  errorMessage = '';

  constructor(
    private readonly authService: AuthService,
    private readonly toastService: ToastService
  ) {}

  requestReset(): void {
    this.loading = true;
    this.errorMessage = '';
    this.message = '';

    const request$: Observable<ForgotPasswordResponse | ForgotPasswordSmsResponse> = this.mode === 'sms'
      ? this.authService.forgotPasswordSms(this.phone.trim())
      : this.authService.forgotPassword(this.email.trim());

    request$.subscribe({
      next: (response: ForgotPasswordResponse | ForgotPasswordSmsResponse) => {
        this.loading = false;
        this.message = response.message;
        if ('resetToken' in response && response.resetToken) {
          this.resetToken = response.resetToken;
        }
        if ('resetCode' in response && response.resetCode) {
          this.resetCode = response.resetCode;
        }
        this.toastService.info('Reset instructions generated.');
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error?.error?.error || 'Failed to request password reset.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  resetPassword(): void {
    this.resetting = true;
    this.errorMessage = '';

    const request$ = this.mode === 'sms'
      ? this.authService.resetPasswordSms(this.phone.trim(), this.resetCode.trim(), this.newPassword)
      : this.authService.resetPassword(this.resetToken.trim(), this.newPassword);

    request$.subscribe({
      next: (response) => {
        this.resetting = false;
        this.message = response.message;
        this.newPassword = '';
        this.toastService.success('Password reset completed.');
      },
      error: (error) => {
        this.resetting = false;
        this.errorMessage = error?.error?.error || 'Failed to reset password.';
        this.toastService.error(this.errorMessage);
      }
    });
  }
}
