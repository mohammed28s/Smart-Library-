import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const workerGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated) {
    return router.createUrlTree(['/login']);
  }

  if (authService.role === 'WORKER') {
    return true;
  }

  return router.createUrlTree(['/dashboard']);
};
