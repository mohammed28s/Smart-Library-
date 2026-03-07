import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginIdentifier = '';
  loginPassword = '';

  loading = false;
  errorMessage = '';
  successMessage = '';
  validationErrors: string[] = [];

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService
  ) {
    if (this.authService.isAuthenticated) {
      this.router.navigateByUrl('/dashboard');
    } else if (this.authService.isGuest) {
      this.router.navigateByUrl('/books');
    }

    this.route.queryParamMap.subscribe((params) => {
      if (params.get('registered') === '1') {
        this.successMessage = 'Registration successful. Please login.';
      }
    });
  }

  login(): void {
    this.clearMessages();
    this.validationErrors = this.validateForm();
    if (this.validationErrors.length > 0) {
      this.toastService.error(this.validationErrors[0]);
      return;
    }

    this.loading = true;

    this.authService
      .login({
        username: this.loginIdentifier.trim(),
        password: this.loginPassword
      })
      .subscribe({
        next: () => {
          this.loading = false;
          this.toastService.success('Login successful.');
          this.router.navigateByUrl('/dashboard');
        },
        error: (error) => {
          this.validationErrors = this.extractBackendDetails(error);
          this.errorMessage = error?.error?.error || 'Login failed.';
          this.toastService.error(this.errorMessage);
          this.loading = false;
        }
      });
  }

  continueAsGuest(): void {
    this.authService.continueAsGuest();
    this.toastService.info('Guest mode enabled. You can browse books.');
    this.router.navigateByUrl('/books');
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.validationErrors = [];
  }

  private validateForm(): string[] {
    const errors: string[] = [];
    const identifier = this.loginIdentifier.trim();
    const password = this.loginPassword;

    if (!identifier) {
      errors.push('Username or email is required.');
    } else {
      const usernameOk = /^[A-Za-z]{3,30}$/.test(identifier);
      const emailOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(identifier);
      if (!usernameOk && !emailOk) {
        errors.push('Username must be 3-30 letters only, or enter a valid email address.');
      }
    }

    if (!password) {
      errors.push('Password is required.');
    } else if (password.length < 8) {
      errors.push('Password must be at least 8 characters.');
    }

    return errors;
  }

  private extractBackendDetails(error: unknown): string[] {
    const details = (error as { error?: { details?: string[] } })?.error?.details;
    return Array.isArray(details) ? details : [];
  }
}
