import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerUsername = '';
  registerEmail = '';
  registerPhone = '';
  registerPassword = '';
  registerFullName = '';

  loading = false;
  errorMessage = '';
  validationErrors: string[] = [];

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService
  ) {}

  register(): void {
    this.errorMessage = '';
    this.validationErrors = this.validateForm();
    if (this.validationErrors.length > 0) {
      this.toastService.error(this.validationErrors[0]);
      return;
    }

    this.loading = true;

    this.authService
      .register({
        username: this.registerUsername.trim(),
        email: this.registerEmail.trim(),
        phone: this.registerPhone.trim() || null,
        password: this.registerPassword,
        fullName: this.registerFullName.trim() || null
      })
      .subscribe({
        next: () => {
          this.authService.logout();
          this.loading = false;
          this.toastService.success('Account created. Please login.');
          this.router.navigate(['/login'], { queryParams: { registered: 1 } });
        },
        error: (error) => {
          const backendDetails = this.extractBackendDetails(error);
          this.validationErrors = backendDetails;
          this.errorMessage = error?.error?.error || 'Registration failed.';
          this.toastService.error(this.errorMessage);
          this.loading = false;
        }
      });
  }

  private validateForm(): string[] {
    const errors: string[] = [];
    const username = this.registerUsername.trim();
    const email = this.registerEmail.trim();
    const phone = this.registerPhone.trim();
    const password = this.registerPassword;
    const fullName = this.registerFullName.trim();

    if (!username) {
      errors.push('Username is required.');
    } else if (!/^[A-Za-z]{3,30}$/.test(username)) {
      errors.push('Username must be 3-30 letters only (no numbers or special characters).');
    }

    if (!email) {
      errors.push('Email is required.');
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      errors.push('Email must be valid.');
    }

    if (phone && !/^\+?[1-9]\d{7,14}$/.test(phone)) {
      errors.push('Phone must be a valid international number.');
    }

    if (!password) {
      errors.push('Password is required.');
    } else {
      if (password.length < 8) {
        errors.push('Password must be at least 8 characters.');
      }
      if (!/[A-Z]/.test(password) || !/[a-z]/.test(password) || !/\d/.test(password)) {
        errors.push('Password must include uppercase, lowercase, and a number.');
      }
    }

    if (fullName && !/^[A-Za-z ]{2,255}$/.test(fullName)) {
      errors.push('Full name must contain letters and spaces only.');
    }

    return errors;
  }

  private extractBackendDetails(error: unknown): string[] {
    const details = (error as { error?: { details?: string[] } })?.error?.details;
    return Array.isArray(details) ? details : [];
  }
}
