import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderItem } from './api.models';
import { OrderItemsService } from './order-items.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-order-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-items.component.html',
  styleUrl: './order-items.component.css'
})
export class OrderItemsComponent implements OnInit {
  items: OrderItem[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  search = '';
  page = 1;
  readonly pageSize = 8;

  editingId: number | null = null;
  form: OrderItem = this.emptyItem();

  constructor(
    private readonly orderItemsService: OrderItemsService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.orderItemsService.getOrderItems().subscribe({
      next: (items) => {
        this.items = items;
        this.page = 1;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to load order items.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: OrderItem = {
      orderId: Number(this.form.orderId),
      bookId: Number(this.form.bookId),
      quantity: Number(this.form.quantity),
      price: Number(this.form.price)
    };

    const request$ = this.editingId
      ? this.orderItemsService.updateOrderItem(this.editingId, payload)
      : this.orderItemsService.createOrderItem(payload);

    request$.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'Order item updated.' : 'Order item created.';
        this.toastService.success(this.successMessage);
        this.saving = false;
        this.editingId = null;
        this.form = this.emptyItem();
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to save order item.';
        this.toastService.error(this.errorMessage);
        this.saving = false;
      }
    });
  }

  edit(item: OrderItem): void {
    this.editingId = item.id || null;
    this.form = { ...item };
  }

  remove(item: OrderItem): void {
    if (!item.id || !confirm(`Delete order item #${item.id}?`)) {
      return;
    }
    this.orderItemsService.deleteOrderItem(item.id).subscribe({
      next: () => {
        this.successMessage = 'Order item deleted.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to delete order item.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  reset(): void {
    this.editingId = null;
    this.form = this.emptyItem();
  }

  private emptyItem(): OrderItem {
    return {
      orderId: 1,
      bookId: 1,
      quantity: 1,
      price: 0
    };
  }

  get filteredItems(): OrderItem[] {
    const q = this.search.trim().toLowerCase();
    if (!q) return this.items;
    return this.items.filter((item) =>
      [item.id, item.orderId, item.bookId, item.quantity, item.price]
        .filter((v) => v !== undefined && v !== null)
        .some((value) => String(value).toLowerCase().includes(q))
    );
  }

  get pagedItems(): OrderItem[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredItems.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredItems.length / this.pageSize));
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
