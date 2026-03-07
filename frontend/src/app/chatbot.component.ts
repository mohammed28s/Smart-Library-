import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AssistantService } from './assistant.service';
import { ToastService } from './toast.service';

type ChatMessage = {
  role: 'assistant' | 'user';
  text: string;
  provider?: string;
};

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.css'
})
export class ChatbotComponent {
  isOpen = false;
  loading = false;
  sendingFeedback = false;
  question = '';
  feedbackComment = '';
  lastFeedbackSent = false;

  readonly messages: ChatMessage[] = [
    {
      role: 'assistant',
      text: 'Hi, I can help with books, orders, payments, and account issues. Ask me anything about SmartLibrary.'
    }
  ];

  constructor(
    private readonly assistantService: AssistantService,
    private readonly toastService: ToastService
  ) {}

  toggle(): void {
    this.isOpen = !this.isOpen;
  }

  send(): void {
    const trimmed = this.question.trim();
    if (!trimmed || this.loading) {
      return;
    }

    this.messages.push({ role: 'user', text: trimmed });
    this.question = '';
    this.loading = true;
    this.lastFeedbackSent = false;
    this.feedbackComment = '';

    this.assistantService
      .chat(trimmed)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (response) => {
          this.messages.push({
            role: 'assistant',
            text: response.answer,
            provider: response.provider
          });
        },
        error: () => {
          this.toastService.error('Chat assistant is unavailable right now.');
        }
      });
  }

  sendFeedback(helpful: boolean): void {
    const lastUser = [...this.messages].reverse().find((m) => m.role === 'user');
    const lastAssistant = [...this.messages].reverse().find((m) => m.role === 'assistant' && m.provider);
    if (!lastUser || !lastAssistant || this.sendingFeedback || this.lastFeedbackSent) {
      return;
    }

    this.sendingFeedback = true;
    this.assistantService
      .feedback({
        question: lastUser.text,
        answer: lastAssistant.text,
        helpful,
        comment: this.feedbackComment.trim()
      })
      .pipe(finalize(() => (this.sendingFeedback = false)))
      .subscribe({
        next: () => {
          this.lastFeedbackSent = true;
          this.toastService.success('Feedback received. Thank you.');
        },
        error: () => {
          this.toastService.error('Could not save feedback.');
        }
      });
  }
}
