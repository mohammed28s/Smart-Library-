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

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService
  ) {
    if (this.authService.isAuthenticated) {
      this.router.navigateByUrl('/dashboard');
    }

    this.route.queryParamMap.subscribe((params) => {
      if (params.get('registered') === '1') {
        this.successMessage = 'Registration successful. Please login.';
      }
    });
  }

  login(): void {
    this.loading = true;
    this.clearMessages();

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
          this.errorMessage = error?.error?.error || 'Login failed.';
          this.toastService.error(this.errorMessage);
          this.loading = false;
        }
      });
  }

  private clearMessages(): void {
    this.errorMessage = '';
  }
}
