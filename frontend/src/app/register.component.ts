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

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService
  ) {}

  register(): void {
    this.loading = true;
    this.errorMessage = '';

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
          this.errorMessage = error?.error?.error || 'Registration failed.';
          this.toastService.error(this.errorMessage);
          this.loading = false;
        }
      });
  }
}
