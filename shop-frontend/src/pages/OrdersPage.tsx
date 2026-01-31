import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Order, Page } from '../types';
import { ordersApi } from '../api';
import './OrdersPage.css';

export function OrdersPage() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [pageInfo, setPageInfo] = useState<Omit<Page<Order>, 'content'> | null>(null);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);

    useEffect(() => {
        const fetchOrders = async () => {
            setLoading(true);
            try {
                const response = await ordersApi.getMyOrders({ page, size: 10 });
                setOrders(response.data.content);
                setPageInfo({
                    totalElements: response.data.totalElements,
                    totalPages: response.data.totalPages,
                    size: response.data.size,
                    number: response.data.number,
                    first: response.data.first,
                    last: response.data.last,
                });
            } catch (error) {
                console.error('Failed to fetch orders:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchOrders();
    }, [page]);

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-IN', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        });
    };

    const getStatusBadge = (status: string) => {
        const statusClasses: Record<string, string> = {
            CREATED: 'badge-primary',
            CONFIRMED: 'badge-primary',
            SHIPPED: 'badge-warning',
            DELIVERED: 'badge-success',
            CANCELLED: 'badge-danger',
        };
        return statusClasses[status] || 'badge-primary';
    };

    if (loading) {
        return (
            <div className="page">
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            </div>
        );
    }

    if (orders.length === 0) {
        return (
            <div className="page orders-page">
                <div className="container">
                    <div className="empty-state">
                        <span className="empty-state-icon">📦</span>
                        <h3 className="empty-state-title">No orders yet</h3>
                        <p>Start shopping and your orders will appear here</p>
                        <Link to="/products" className="btn btn-primary mt-3">
                            Browse Products
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="page orders-page">
            <div className="container">
                <div className="page-header">
                    <h1 className="page-title">My Orders</h1>
                    <p className="page-subtitle">Track and manage your orders</p>
                </div>

                <div className="orders-list">
                    {orders.map(order => (
                        <Link key={order.id} to={`/orders/${order.id}`} className="order-card">
                            <div className="order-header">
                                <div className="order-id">
                                    <span className="label">Order</span>
                                    <span className="value">#{order.id}</span>
                                </div>
                                <span className={`badge ${getStatusBadge(order.status)}`}>
                                    {order.status}
                                </span>
                            </div>

                            <div className="order-body">
                                <div className="order-items-preview">
                                    {order.items.slice(0, 3).map(item => (
                                        <span key={item.id} className="item-preview">
                                            {item.productName}
                                        </span>
                                    ))}
                                    {order.items.length > 3 && (
                                        <span className="more-items">+{order.items.length - 3} more</span>
                                    )}
                                </div>
                            </div>

                            <div className="order-footer">
                                <div className="order-date">
                                    <span className="label">Placed on</span>
                                    <span className="value">{formatDate(order.createdAt)}</span>
                                </div>
                                <div className="order-total">
                                    <span className="label">Total</span>
                                    <span className="value">{formatPrice(order.totalAmount)}</span>
                                </div>
                            </div>
                        </Link>
                    ))}
                </div>

                {/* Pagination */}
                {pageInfo && pageInfo.totalPages > 1 && (
                    <div className="pagination">
                        <button
                            className="btn btn-secondary"
                            disabled={pageInfo.first}
                            onClick={() => setPage(p => p - 1)}
                        >
                            ← Previous
                        </button>
                        <span className="pagination-info">
                            Page {pageInfo.number + 1} of {pageInfo.totalPages}
                        </span>
                        <button
                            className="btn btn-secondary"
                            disabled={pageInfo.last}
                            onClick={() => setPage(p => p + 1)}
                        >
                            Next →
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
