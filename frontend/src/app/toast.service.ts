import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastMessage {
  id: number;
  type: ToastType;
  text: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  private readonly messagesSubject = new BehaviorSubject<ToastMessage[]>([]);
  readonly messages$ = this.messagesSubject.asObservable();

  success(text: string): void {
    this.push('success', text);
  }

  error(text: string): void {
    this.push('error', text);
  }

  info(text: string): void {
    this.push('info', text);
  }

  dismiss(id: number): void {
    this.messagesSubject.next(this.messagesSubject.value.filter((m) => m.id !== id));
  }

  private push(type: ToastType, text: string): void {
    const id = ++this.counter;
    const next = [...this.messagesSubject.value, { id, type, text }];
    this.messagesSubject.next(next);
    setTimeout(() => this.dismiss(id), 3500);
  }
}
