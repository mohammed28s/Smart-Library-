import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';
import { BooksComponent } from './books.component';
import { DashboardComponent } from './dashboard.component';
import { ForgotPasswordComponent } from './forgot-password.component';
import { LoginComponent } from './login.component';
import { OrderItemsComponent } from './order-items.component';
import { OrdersComponent } from './orders.component';
import { PaymentsComponent } from './payments.component';
import { RegisterComponent } from './register.component';
import { UsersComponent } from './users.component';
import { workerGuard } from './worker.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'books', component: BooksComponent, canActivate: [authGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [authGuard] },
  { path: 'order-items', component: OrderItemsComponent, canActivate: [authGuard] },
  { path: 'payments', component: PaymentsComponent, canActivate: [authGuard] },
  { path: 'users', component: UsersComponent, canActivate: [workerGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
