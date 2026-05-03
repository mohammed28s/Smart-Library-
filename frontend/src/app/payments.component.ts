import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Payment, PaymentStatus } from './api.models';
import { AuthService } from './auth.service';
import { PaymentsService } from './payments.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.css'
})
export class PaymentsComponent implements OnInit {
  payments: Payment[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  search = '';
  page = 1;
  readonly pageSize = 8;

  statuses: PaymentStatus[] = ['SUCCEEDED', 'REFUNDED'];
  editingId: number | null = null;
  form: Payment = this.emptyPayment();

  constructor(
    private readonly paymentsService: PaymentsService,
    private readonly authService: AuthService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.paymentsService.getPayments().subscribe({
      next: (payments) => {
        this.payments = payments;
        this.page = 1;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to load payments.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: Payment = {
      orderId: Number(this.form.orderId),
      provider: this.form.provider.trim(),
      providerPaymentId: this.form.providerPaymentId?.trim() || null,
      amount: Number(this.form.amount),
      status: this.form.status
    };

    const request$ = this.editingId
      ? this.paymentsService.updatePayment(this.editingId, payload)
      : this.paymentsService.createPayment(payload);

    request$.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'Payment updated.' : 'Payment created.';
        this.toastService.success(this.successMessage);
        this.saving = false;
        this.reset();
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to save payment.';
        this.toastService.error(this.errorMessage);
        this.saving = false;
      }
    });
  }

  edit(payment: Payment): void {
    this.editingId = payment.id || null;
    this.form = { ...payment };
  }

  remove(payment: Payment): void {
    if (!payment.id || !confirm(`Delete payment #${payment.id}?`)) {
      return;
    }
    this.paymentsService.deletePayment(payment.id).subscribe({
      next: () => {
        this.successMessage = 'Payment deleted.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to delete payment.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  reset(): void {
    this.editingId = null;
    this.form = this.emptyPayment();
  }

  get isWorker(): boolean {
    return this.authService.role === 'WORKER';
  }

  private emptyPayment(): Payment {
    return {
      orderId: 1,
      provider: 'stripe',
      providerPaymentId: '',
      amount: 0,
      status: 'SUCCEEDED'
    };
  }

  get filteredPayments(): Payment[] {
    const q = this.search.trim().toLowerCase();
    let list = this.payments;

    // Normal users shouldn't see all payments.
    if (!this.isWorker) {
      list = []; // Simple protection for demo
    }

    if (!q) return list;
    return list.filter((payment) =>
      [payment.id, payment.orderId, payment.provider, payment.status, payment.providerPaymentId]
        .filter((v) => v !== undefined && v !== null)
        .some((value) => String(value).toLowerCase().includes(q))
    );
  }


  get pagedPayments(): Payment[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredPayments.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredPayments.length / this.pageSize));
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
