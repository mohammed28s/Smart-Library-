import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LibraryOrder, OrderStatus, OrderType } from './api.models';
import { AuthService } from './auth.service';
import { OrdersService } from './orders.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  orders: LibraryOrder[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  search = '';
  page = 1;
  readonly pageSize = 8;

  orderStatuses: OrderStatus[] = ['CREATED', 'PAID', 'REFUND_REQUESTED', 'CANCELLED', 'REFUNDED'];
  orderTypes: OrderType[] = ['BUY', 'RENT'];
  scannedOrder: LibraryOrder | null = null;
  scanBarcode = '';

  form: LibraryOrder = this.emptyOrder();
  editingId: number | null = null;

  constructor(
    private readonly ordersService: OrdersService,
    private readonly authService: AuthService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.ordersService.getOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.page = 1;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to load orders.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: LibraryOrder = {
      userId: Number(this.form.userId),
      total: Number(this.form.total),
      status: this.form.status,
      type: this.form.type,
      barcode: this.form.barcode?.trim() || null,
      rentalStartDate: this.form.type === 'RENT' ? this.form.rentalStartDate || null : null,
      dueDate: this.form.type === 'RENT' ? this.form.dueDate || null : null
    };

    const request$ = this.editingId
      ? this.ordersService.updateOrder(this.editingId, payload)
      : this.ordersService.createOrder(payload);

    request$.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'Order updated.' : 'Order created.';
        this.toastService.success(this.successMessage);
        this.saving = false;
        this.editingId = null;
        this.form = this.emptyOrder();
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to save order.';
        this.toastService.error(this.errorMessage);
        this.saving = false;
      }
    });
  }

  edit(order: LibraryOrder): void {
    this.editingId = order.id || null;
    this.form = {
      userId: order.userId,
      total: order.total,
      status: order.status,
      type: order.type,
      barcode: order.barcode || '',
      rentalStartDate: order.rentalStartDate || null,
      dueDate: order.dueDate || null
    };
  }

  remove(order: LibraryOrder): void {
    if (!order.id || !confirm(`Delete order #${order.id}?`)) {
      return;
    }
    this.ordersService.deleteOrder(order.id).subscribe({
      next: () => {
        this.successMessage = 'Order deleted.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to delete order.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  reset(): void {
    this.editingId = null;
    this.form = this.emptyOrder();
  }

  cancel(order: LibraryOrder): void {
    if (!order.id) {
      return;
    }
    this.ordersService.cancelOrder(order.id).subscribe({
      next: () => {
        this.successMessage = 'Order cancelled.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to cancel order.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  requestRefund(order: LibraryOrder): void {
    if (!order.id) {
      return;
    }
    this.ordersService.requestRefund(order.id).subscribe({
      next: () => {
        this.successMessage = 'Refund request submitted.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to request refund.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  approveRefund(order: LibraryOrder): void {
    if (!order.id || !this.isWorker) {
      return;
    }
    this.ordersService.approveRefund(order.id).subscribe({
      next: () => {
        this.successMessage = 'Refund approved.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to approve refund.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  scanOrderBarcode(): void {
    const barcode = this.scanBarcode.trim();
    if (!barcode || !this.isWorker) {
      return;
    }
    this.ordersService.scanBarcode(barcode).subscribe({
      next: (order) => {
        this.scannedOrder = order;
        this.successMessage = `Barcode matched order #${order.id}`;
        this.toastService.success(this.successMessage);
      },
      error: (error) => {
        this.scannedOrder = null;
        this.errorMessage = error?.error?.error || 'Barcode not found.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  get isWorker(): boolean {
    return this.authService.role === 'WORKER';
  }

  private emptyOrder(): LibraryOrder {
    return {
      userId: 1,
      total: 0,
      status: 'CREATED',
      type: 'BUY',
      barcode: '',
      rentalStartDate: null,
      dueDate: null
    };
  }

  get filteredOrders(): LibraryOrder[] {
    const q = this.search.trim().toLowerCase();
    let list = this.orders;
    if (!this.isWorker) {
      const currentUserId = this.authService.userId;
      list = list.filter((order) => order.userId === currentUserId);
    }
    if (!q) return list;
    return list.filter((order) =>
      [order.id, order.userId, order.status, order.type, order.barcode]
        .filter((v) => v !== undefined && v !== null)
        .some((value) => String(value).toLowerCase().includes(q))
    );
  }

  get pagedOrders(): LibraryOrder[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredOrders.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredOrders.length / this.pageSize));
  }

  onSearchChange(): void {
    this.page = 1;
  }

  prevPage(): void {
    this.page = Math.max(1, this.page - 1);
  }

  nextPage(): void {
    this.page = Math.min(this.totalPages, this.page + 1);
  }
}
