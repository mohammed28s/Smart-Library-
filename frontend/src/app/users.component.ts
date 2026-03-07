import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserDto, UserRole } from './api.models';
import { ToastService } from './toast.service';
import { UsersService } from './users.service';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  users: UserDto[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';
  search = '';
  page = 1;
  readonly pageSize = 8;

  roles: UserRole[] = ['USER', 'WORKER'];
  editingId: number | null = null;
  form: UserDto = this.emptyUser();

  constructor(
    private readonly usersService: UsersService,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.usersService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.page = 1;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  submit(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload: UserDto = {
      username: this.form.username.trim(),
      email: this.form.email.trim(),
      phone: this.form.phone?.trim() || null,
      fullName: this.form.fullName?.trim() || null,
      role: this.form.role,
      password: this.form.password
    };

    const request$ = this.editingId
      ? this.usersService.updateUser(this.editingId, payload)
      : this.usersService.createUser(payload);

    request$.subscribe({
      next: () => {
        this.successMessage = this.editingId ? 'User updated.' : 'User created.';
        this.toastService.success(this.successMessage);
        this.saving = false;
        this.reset();
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to save user.';
        this.toastService.error(this.errorMessage);
        this.saving = false;
      }
    });
  }

  edit(user: UserDto): void {
    this.editingId = user.id || null;
    this.form = {
      username: user.username,
      email: user.email,
      phone: user.phone || '',
      fullName: user.fullName || '',
      role: user.role,
      password: ''
    };
  }

  remove(user: UserDto): void {
    if (!user.id || !confirm(`Delete user "${user.username}"?`)) {
      return;
    }
    this.usersService.deleteUser(user.id).subscribe({
      next: () => {
        this.successMessage = 'User deleted.';
        this.toastService.success(this.successMessage);
        this.load();
      },
      error: (error) => {
        this.errorMessage = error?.error?.error || 'Failed to delete user.';
        this.toastService.error(this.errorMessage);
      }
    });
  }

  reset(): void {
    this.editingId = null;
    this.form = this.emptyUser();
  }

  private emptyUser(): UserDto {
    return {
      username: '',
      email: '',
      phone: '',
      password: '',
      fullName: '',
      role: 'USER'
    };
  }

  get filteredUsers(): UserDto[] {
    const q = this.search.trim().toLowerCase();
    if (!q) return this.users;
    return this.users.filter((user) =>
      [user.id, user.username, user.email, user.phone, user.fullName, user.role]
        .filter((v) => v !== undefined && v !== null)
        .some((value) => String(value).toLowerCase().includes(q))
    );
  }

  get pagedUsers(): UserDto[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredUsers.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.filteredUsers.length / this.pageSize));
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
