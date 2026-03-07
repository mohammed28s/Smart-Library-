import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Book } from './api.models';

@Injectable({ providedIn: 'root' })
export class BooksService {
  private readonly apiBaseUrl = '';

  constructor(private readonly http: HttpClient) {}

  getBooks(): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiBaseUrl}/api/books`);
  }

  createBook(payload: Book): Observable<Book> {
    return this.http.post<Book>(`${this.apiBaseUrl}/api/books`, payload);
  }

  updateBook(id: number, payload: Book): Observable<Book> {
    return this.http.put<Book>(`${this.apiBaseUrl}/api/books/${id}`, payload);
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBaseUrl}/api/books/${id}`);
  }

  getBarcodeImage(id: number): Observable<Blob> {
    return this.http.get(`${this.apiBaseUrl}/api/books/${id}/barcode`, {
      responseType: 'blob'
    });
  }
}
