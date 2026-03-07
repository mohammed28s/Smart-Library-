import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';
import { ChatbotComponent } from './chatbot.component';
import { ToastCenterComponent } from './toast-center.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, ToastCenterComponent, ChatbotComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'SmartLibrary';
  menuOpen = false;

  constructor(
    public readonly authService: AuthService,
    private readonly router: Router
  ) {}

  // Backward-compatible alias for older templates still using authToken.
  get authToken(): string {
    return this.authService.token;
  }

  logout(): void {
    this.authService.logout();
    this.menuOpen = false;
    this.router.navigateByUrl('/login');
  }

  closeMenu(): void {
    this.menuOpen = false;
  }

  guestLogoutLabel(): string {
    return this.authService.isGuest ? 'Exit Guest Mode' : 'Logout';
  }
}
