import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Book } from './api.models';
import { BooksService } from './books.service';
import { ToastService } from './toast.service';

interface BookView extends Book {
  barcodeImageUrl?: string;
  barcodeLoading?: boolean;
}

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './books.component.html',
  styleUrl: './books.component.css'
})
export class BooksComponent implements OnInit, OnDestroy {
  books: BookView[] = [];
  booksLoading = false;
  errorMessage = '';
  successMessage = '';
  search = '';
  page = 1;
  readonly pageSize = 6;

  form: Book = this.emptyBook();
  editingId: number | null = null;
  saving = false;

  get canManageBooks(): boolean {
    return this.authService.isAuthenticated;
  }

  constructor(
    private readonly booksService: BooksService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  ngOnDestroy(): void {
    this.revokeBarcodeUrls();
  }

  loadBooks(): void {
    this.booksLoading = true;
    this.errorMessage = '';

    this.booksService.getBooks().subscribe({
      next: (books) => {
        this.revokeBarcodeUrls();
        this.books = books.map((book) => ({ ...book }));
        this.page = 1;
        this.booksLoading = false;
      },
      error: (error) => {
        this.booksLoading = false;
        if (error?.status === 401 || error?.status === 403) {
          this.authService.logout();
          this.router.navigateByUrl('/login');
          return;
        }
        this.errorMessage = error?.error?.error || 'Failed to load books.';
      }
    });
  }

  submitBook(): void {
    if (!this.canManageBooks) {
      this.errorMessage = 'Guest mode is read-only. Login to manage books.';
      this.toastService.info(this.errorMessage);
      return;
    }
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: Book = {
      title: this.form.title.trim(),
      author: this.form.author?.trim() || null,
      isbn: this.form.isbn?.trim() || null,
      price: Number(this.form.price),
      stock: Number(this.form.stock || 0),
      description: this.form.description?.trim() || null
    };

    const request$ = this.editingId
      ? this.booksService.updateBook(this.editingId, payload)
      : this.booksService.createBook(payload);

    request$.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'Book updated.' : 'Book created.';
        this.toastService.success(this.successMessage);
        this.saving = false;
        this.resetForm();
        this.loadBooks();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to save book.';
        this.toastService.error(this.errorMessage);
        this.saving = false;
      }
    });
  }

  startEdit(book: BookView): void {
    if (!this.canManageBooks) {
      this.toastService.info('Guest mode is read-only. Login to edit books.');
      return;
    }
    this.editingId = book.id || null;
    this.form = {
      title: book.title,
      author: book.author || '',
      isbn: book.isbn || '',
      price: book.price,
      stock: book.stock || 0,
      description: book.description || ''
    };
    this.successMessage = '';
    this.errorMessage = '';
  }

  deleteBook(book: BookView): void {
    if (!this.canManageBooks) {
      this.toastService.info('Guest mode is read-only. Login to delete books.');
      return;
    }
    if (!book.id || !confirm(`Delete "${book.title}"?`)) {
      return;
    }
    this.errorMessage = '';
    this.successMessage = '';
    this.booksService.deleteBook(book.id).subscribe({
      next: () => {
        this.successMessage = 'Book deleted.';
        this.toastService.success(this.successMessage);
        this.loadBooks();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to delete book.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form = this.emptyBook();
  }

  loadBarcode(book: BookView): void {
    if (book.barcodeLoading || !book.id) {
      return;
    }
    book.barcodeLoading = true;

    this.booksService.getBarcodeImage(book.id).subscribe({
      next: (blob) => {
        if (book.barcodeImageUrl) {
          URL.revokeObjectURL(book.barcodeImageUrl);
        }
        book.barcodeImageUrl = URL.createObjectURL(blob);
        book.barcodeLoading = false;
      },
      error: (error) => {
        book.barcodeLoading = false;
        if (error?.status === 401 || error?.status === 403) {
          this.authService.logout();
          this.router.navigateByUrl('/login');
          return;
        }
        this.errorMessage = 'Failed to load barcode image.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  trackByBookId(_: number, book: BookView): number {
    return book.id || 0;
  }

  get filteredBooks(): BookView[] {
    const q = this.search.trim().toLowerCase();
    if (!q) {
      return this.books;
    }
    return this.books.filter((book) =>
      [book.title, book.author, book.isbn, book.barcode]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(q))
    );
  }

  get pagedBooks(): BookView[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredBooks.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredBooks.length / this.pageSize));
  }

  prevPage(): void {
    this.page = Math.max(1, this.page - 1);
  }

  nextPage(): void {
    this.page = Math.min(this.totalPages, this.page + 1);
  }

  onSearchChange(): void {
    this.page = 1;
  }

  private emptyBook(): Book {
    return {
      title: '',
      author: '',
      isbn: '',
      price: 1,
      stock: 0,
      description: ''
    };
  }

  private revokeBarcodeUrls(): void {
    for (const book of this.books) {
      if (book.barcodeImageUrl) {
        URL.revokeObjectURL(book.barcodeImageUrl);
      }
    }
  }
}
