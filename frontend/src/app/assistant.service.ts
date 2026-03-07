import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface AssistantChatResponse {
  answer: string;
  provider: string;
}

export interface AssistantFeedbackResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AssistantService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  chat(question: string): Observable<AssistantChatResponse> {
    return this.http.post<AssistantChatResponse>(`${this.apiBaseUrl}/api/assistant/chat`, { question });
  }

  feedback(payload: {
    question: string;
    answer: string;
    helpful: boolean;
    comment?: string;
  }): Observable<AssistantFeedbackResponse> {
    return this.http.post<AssistantFeedbackResponse>(`${this.apiBaseUrl}/api/assistant/feedback`, payload);
  }
}
