import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { AuthService } from './auth.service';
import { BooksService } from './books.service';
import { OrderItemsService } from './order-items.service';
import { OrdersService } from './orders.service';
import { PaymentsService } from './payments.service';
import { UsersService } from './users.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  loading = false;
  errorMessage = '';

  totalBooks = 0;
  totalUsers = 0;
  totalOrders = 0;
  totalOrderItems = 0;
  totalPayments = 0;
  inventoryValue = 0;
  totalPaidRevenue = 0;
  isWorker = false;

  constructor(
    private readonly authService: AuthService,
    private readonly booksService: BooksService,
    private readonly usersService: UsersService,
    private readonly ordersService: OrdersService,
    private readonly orderItemsService: OrderItemsService,
    private readonly paymentsService: PaymentsService
  ) {}

  ngOnInit(): void {
    this.isWorker = this.authService.role === 'WORKER';
    this.refresh();
  }

  refresh(): void {
    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      books: this.booksService.getBooks(),
      users: this.isWorker ? this.usersService.getUsers() : of([]),
      orders: this.ordersService.getOrders(),
      orderItems: this.orderItemsService.getOrderItems(),
      payments: this.paymentsService.getPayments()
    }).subscribe({
      next: ({ books, users, orders, orderItems, payments }) => {
        this.totalBooks = books.length;
        this.totalUsers = users.length;
        this.totalOrders = orders.length;
        this.totalOrderItems = orderItems.length;
        this.totalPayments = payments.length;
        this.inventoryValue = books.reduce((sum, b) => sum + (b.price || 0) * (b.stock || 0), 0);
        this.totalPaidRevenue = payments
          .filter((payment) => payment.status === 'SUCCEEDED')
          .reduce((sum, payment) => sum + (payment.amount || 0), 0);
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to load dashboard metrics.';
        this.loading = false;
      }
    });
  }
}
