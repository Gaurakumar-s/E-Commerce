import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Order } from '../types';
import { ordersApi } from '../api';
import './OrderDetailPage.css';

export function OrderDetailPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const location = useLocation();
    const [order, setOrder] = useState<Order | null>(null);
    const [loading, setLoading] = useState(true);
    const [cancelling, setCancelling] = useState(false);

    const isNewOrder = (location.state as { newOrder?: boolean })?.newOrder;

    useEffect(() => {
        const fetchOrder = async () => {
            if (!id) return;
            try {
                const response = await ordersApi.getById(Number(id));
                setOrder(response.data);
            } catch (error) {
                console.error('Failed to fetch order:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchOrder();
    }, [id]);

    const handleCancel = async () => {
        if (!order) return;
        if (!window.confirm('Are you sure you want to cancel this order?')) return;

        setCancelling(true);
        try {
            const response = await ordersApi.cancel(order.id);
            setOrder(response.data);
        } catch (error) {
            console.error('Failed to cancel order:', error);
        } finally {
            setCancelling(false);
        }
    };

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
            hour: '2-digit',
            minute: '2-digit',
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

    if (!order) {
        return (
            <div className="page">
                <div className="container">
                    <div className="empty-state">
                        <span className="empty-state-icon">404</span>
                        <h3 className="empty-state-title">Order Not Found</h3>
                        <button className="btn btn-primary" onClick={() => navigate('/orders')}>
                            View All Orders
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    const canCancel = ['CREATED', 'CONFIRMED'].includes(order.status);

    return (
        <div className="page order-detail-page">
            <div className="container">
                {isNewOrder && (
                    <div className="order-success-banner">
                        <span className="success-icon">✓</span>
                        <div>
                            <strong>Order Placed Successfully!</strong>
                            <p>Thank you for your purchase. You'll receive a confirmation email shortly.</p>
                        </div>
                    </div>
                )}

                <button className="back-button" onClick={() => navigate('/orders')}>
                    ← Back to Orders
                </button>

                <div className="order-detail-layout">
                    <div className="order-main">
                        <div className="order-header-section">
                            <div className="order-title">
                                <h1>Order #{order.id}</h1>
                                <span className={`badge ${getStatusBadge(order.status)}`}>
                                    {order.status}
                                </span>
                            </div>
                            <p className="order-date">Placed on {formatDate(order.createdAt)}</p>
                        </div>

                        <div className="order-items-section">
                            <h2>Order Items</h2>
                            <div className="order-items-list">
                                {order.items.map(item => (
                                    <div key={item.id} className="order-item">
                                        <div className="order-item-image">📦</div>
                                        <div className="order-item-info">
                                            <span className="order-item-name">{item.productName}</span>
                                            <span className="order-item-price">
                                                {formatPrice(item.priceEach)} × {item.quantity}
                                            </span>
                                        </div>
                                        <div className="order-item-total">
                                            {formatPrice(item.subtotal)}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {canCancel && (
                            <div className="order-actions">
                                <button
                                    className="btn btn-danger"
                                    onClick={handleCancel}
                                    disabled={cancelling}
                                >
                                    {cancelling ? 'Cancelling...' : 'Cancel Order'}
                                </button>
                            </div>
                        )}
                    </div>

                    <aside className="order-sidebar">
                        <div className="order-summary-card">
                            <h3>Order Summary</h3>

                            <div className="summary-row">
                                <span>Subtotal</span>
                                <span>{formatPrice(order.totalAmount)}</span>
                            </div>
                            <div className="summary-row">
                                <span>Shipping</span>
                                <span className="free-shipping">Free</span>
                            </div>
                            <div className="summary-row total">
                                <span>Total</span>
                                <span>{formatPrice(order.totalAmount)}</span>
                            </div>
                        </div>

                        <div className="payment-card">
                            <h3>Payment</h3>
                            <div className="payment-info">
                                <span className={`badge ${order.paymentStatus === 'PAID' ? 'badge-success' : 'badge-warning'}`}>
                                    {order.paymentStatus}
                                </span>
                                <span className="payment-ref">Ref: {order.paymentReference}</span>
                            </div>
                        </div>
                    </aside>
                </div>
            </div>
        </div>
    );
}
