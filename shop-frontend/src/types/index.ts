// User types
export interface User {
    id: number;
    name: string;
    email: string;
    addressLine1?: string;
    addressLine2?: string;
    city?: string;
    state?: string;
    country?: string;
    zipCode?: string;
    roles: string[];
    createdAt: string;
    updatedAt: string;
}

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    addressLine1?: string;
    addressLine2?: string;
    city?: string;
    state?: string;
    country?: string;
    zipCode?: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
}

// Product types
export interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    stockQuantity: number;
    categoryId: number;
    categoryName: string;
    imageUrl?: string;
    active: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface ProductRequest {
    name: string;
    description: string;
    price: number;
    stockQuantity: number;
    categoryId: number;
    active?: boolean;
}

// Category types
export interface Category {
    id: number;
    name: string;
    parentCategoryId?: number;
    active: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface CategoryRequest {
    name: string;
    parentCategoryId?: number;
    active?: boolean;
}

// Cart types
export interface CartItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    priceAtAddTime: number;
    lineTotal: number;
}

export interface Cart {
    id: number;
    userId: number;
    items: CartItem[];
    totalAmount: number;
    lastUpdated: string;
}

export interface AddCartItemRequest {
    productId: number;
    quantity: number;
}

export interface UpdateCartItemRequest {
    quantity: number;
}

// Order types
export type OrderStatus = 'CREATED' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';

export interface OrderItem {
    id: number;
    productId: number;
    productName: string;
    quantity: number;
    priceEach: number;
    subtotal: number;
}

export interface Order {
    id: number;
    userId: number;
    totalAmount: number;
    status: OrderStatus;
    paymentStatus: PaymentStatus;
    paymentReference: string;
    createdAt: string;
    items: OrderItem[];
}

export interface PlaceOrderRequest {
    paymentReference?: string;
}

// Analytics types
export interface TopProduct {
    productId: number;
    productName: string;
    totalQuantitySold: number;
    totalRevenue: number;
}

export interface RevenueData {
    date: string;
    totalRevenue: number;
    orderCount: number;
}

// Pagination
export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
}

// Error response
export interface ApiError {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
    validationErrors?: Record<string, string>;
}
