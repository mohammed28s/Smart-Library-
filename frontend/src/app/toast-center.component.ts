import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast-center',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-center.component.html',
  styleUrl: './toast-center.component.css'
})
export class ToastCenterComponent {
  constructor(public readonly toastService: ToastService) {}
}
