export type UserRole = 'USER' | 'WORKER';
export type OrderType = 'BUY' | 'RENT';
export type OrderStatus = 'CREATED' | 'PAID' | 'CANCELLED' | 'REFUNDED';
export type PaymentStatus = 'SUCCEEDED' | 'REFUNDED';

export interface Book {
  id?: number;
  title: string;
  author?: string | null;
  isbn?: string | null;
  price: number;
  stock?: number | null;
  description?: string | null;
  barcode?: string | null;
}

export interface UserDto {
  id?: number;
  username: string;
  email: string;
  phone?: string | null;
  password?: string;
  fullName?: string | null;
  role: UserRole;
}

export interface LibraryOrder {
  id?: number;
  userId: number;
  total: number;
  status: OrderStatus;
  type: OrderType;
  barcode?: string | null;
  createdAt?: string;
}

export interface OrderItem {
  id?: number;
  orderId: number;
  bookId: number;
  quantity: number;
  price: number;
}

export interface Payment {
  id?: number;
  orderId: number;
  provider: string;
  providerPaymentId?: string | null;
  amount: number;
  status: PaymentStatus;
  createdAt?: string;
}
