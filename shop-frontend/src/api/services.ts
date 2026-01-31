import api from './client';
import {
    User,
    RegisterRequest,
    LoginRequest,
    AuthResponse,
    Product,
    ProductRequest,
    Category,
    CategoryRequest,
    Cart,
    AddCartItemRequest,
    UpdateCartItemRequest,
    Order,
    PlaceOrderRequest,
    TopProduct,
    RevenueData,
    Page,
    OrderStatus,
} from '../types';

// Auth API
export const authApi = {
    register: (data: RegisterRequest) =>
        api.post<User>('/auth/register', data),

    login: (data: LoginRequest) =>
        api.post<AuthResponse>('/auth/login', data),

    me: () =>
        api.get<User>('/auth/me'),
};

// Products API
export const productsApi = {
    getAll: (params?: {
        categoryId?: number;
        search?: string;
        minPrice?: number;
        maxPrice?: number;
        active?: boolean;
        inStock?: boolean;
        page?: number;
        size?: number;
        sort?: string;
    }) => api.get<Page<Product>>('/api/products', { params }),

    getById: (id: number) =>
        api.get<Product>(`/api/products/${id}`),

    create: (data: ProductRequest) =>
        api.post<Product>('/api/products', data),

    update: (id: number, data: ProductRequest) =>
        api.put<Product>(`/api/products/${id}`, data),

    delete: (id: number) =>
        api.delete(`/api/products/${id}`),

    uploadImage: (id: number, file: File) => {
        const formData = new FormData();
        formData.append('file', file);
        return api.post<Product>(`/api/products/${id}/image`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
    },
};

// Categories API
export const categoriesApi = {
    getAll: () =>
        api.get<Category[]>('/api/categories'),

    create: (data: CategoryRequest) =>
        api.post<Category>('/api/categories', data),

    update: (id: number, data: CategoryRequest) =>
        api.put<Category>(`/api/categories/${id}`, data),

    delete: (id: number) =>
        api.delete(`/api/categories/${id}`),
};

// Cart API
export const cartApi = {
    get: () =>
        api.get<Cart>('/api/cart'),

    addItem: (data: AddCartItemRequest) =>
        api.post<Cart>('/api/cart/items', data),

    updateItem: (itemId: number, data: UpdateCartItemRequest) =>
        api.put<Cart>(`/api/cart/items/${itemId}`, data),

    removeItem: (itemId: number) =>
        api.delete<Cart>(`/api/cart/items/${itemId}`),

    clear: () =>
        api.delete<Cart>('/api/cart'),
};

// Orders API
export const ordersApi = {
    place: (data: PlaceOrderRequest) =>
        api.post<Order>('/api/orders', data),

    getById: (id: number) =>
        api.get<Order>(`/api/orders/${id}`),

    getMyOrders: (params?: { page?: number; size?: number }) =>
        api.get<Page<Order>>('/api/orders/my-orders', { params }),

    cancel: (id: number) =>
        api.put<Order>(`/api/orders/${id}/cancel`),

    // Admin
    getAll: (params?: {
        status?: OrderStatus;
        startDate?: string;
        endDate?: string;
        page?: number;
        size?: number;
    }) => api.get<Page<Order>>('/api/orders', { params }),

    updateStatus: (id: number, status: OrderStatus) =>
        api.put<Order>(`/api/orders/${id}/status`, null, { params: { status } }),
};

// Analytics API
export const analyticsApi = {
    topProductsByRevenue: (params?: { limit?: number; startDate?: string; endDate?: string }) =>
        api.get<TopProduct[]>('/api/analytics/top-products/revenue', { params }),

    topProductsByQuantity: (params?: { limit?: number; startDate?: string; endDate?: string }) =>
        api.get<TopProduct[]>('/api/analytics/top-products/quantity', { params }),

    dailyRevenue: (params?: { startDate?: string; endDate?: string }) =>
        api.get<RevenueData[]>('/api/analytics/revenue/daily', { params }),

    totalRevenue: (params?: { startDate?: string; endDate?: string }) =>
        api.get<number>('/api/analytics/revenue/total', { params }),

    totalOrders: (params?: { startDate?: string; endDate?: string }) =>
        api.get<number>('/api/analytics/orders/total', { params }),

    lowStockProducts: (threshold?: number) =>
        api.get<Product[]>('/api/analytics/products/low-stock', { params: { threshold } }),

    activeUsers: (params?: { startDate?: string; endDate?: string }) =>
        api.get<number>('/api/analytics/users/active', { params }),
};
