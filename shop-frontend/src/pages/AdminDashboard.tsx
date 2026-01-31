import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { TopProduct, Product, Order, Page } from '../types';
import { analyticsApi, productsApi, ordersApi } from '../api';
import './AdminDashboard.css';

export function AdminDashboard() {
    const [stats, setStats] = useState({
        totalRevenue: 0,
        totalOrders: 0,
        activeUsers: 0,
    });
    const [topProducts, setTopProducts] = useState<TopProduct[]>([]);
    const [lowStockProducts, setLowStockProducts] = useState<Product[]>([]);
    const [recentOrders, setRecentOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [
                    revenueRes,
                    ordersCountRes,
                    activeUsersRes,
                    topProductsRes,
                    lowStockRes,
                    recentOrdersRes,
                ] = await Promise.all([
                    analyticsApi.totalRevenue(),
                    analyticsApi.totalOrders(),
                    analyticsApi.activeUsers(),
                    analyticsApi.topProductsByRevenue({ limit: 5 }),
                    analyticsApi.lowStockProducts(10),
                    ordersApi.getAll({ size: 5 }),
                ]);

                setStats({
                    totalRevenue: revenueRes.data,
                    totalOrders: ordersCountRes.data,
                    activeUsers: activeUsersRes.data,
                });
                setTopProducts(topProductsRes.data);
                setLowStockProducts(lowStockRes.data);
                setRecentOrders(recentOrdersRes.data.content);
            } catch (error) {
                console.error('Failed to fetch dashboard data:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchDashboardData();
    }, []);

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-IN', {
            month: 'short',
            day: 'numeric',
        });
    };

    const getStatusBadge = (status: string) => {
        const classes: Record<string, string> = {
            CREATED: 'badge-primary',
            CONFIRMED: 'badge-primary',
            SHIPPED: 'badge-warning',
            DELIVERED: 'badge-success',
            CANCELLED: 'badge-danger',
        };
        return classes[status] || 'badge-primary';
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

    return (
        <div className="page admin-dashboard">
            <div className="container">
                <div className="page-header">
                    <div>
                        <h1 className="page-title">Admin Dashboard</h1>
                        <p className="page-subtitle">Overview of your store performance</p>
                    </div>
                    <div className="header-actions">
                        <Link to="/admin/products" className="btn btn-primary">
                            Manage Products
                        </Link>
                    </div>
                </div>

                {/* Stats Cards */}
                <div className="stats-grid">
                    <div className="stat-card">
                        <span className="stat-icon">💰</span>
                        <div className="stat-info">
                            <span className="stat-value">{formatPrice(stats.totalRevenue)}</span>
                            <span className="stat-label">Total Revenue</span>
                        </div>
                    </div>
                    <div className="stat-card">
                        <span className="stat-icon">📦</span>
                        <div className="stat-info">
                            <span className="stat-value">{stats.totalOrders}</span>
                            <span className="stat-label">Total Orders</span>
                        </div>
                    </div>
                    <div className="stat-card">
                        <span className="stat-icon">👥</span>
                        <div className="stat-info">
                            <span className="stat-value">{stats.activeUsers}</span>
                            <span className="stat-label">Active Users</span>
                        </div>
                    </div>
                </div>

                <div className="dashboard-grid">
                    {/* Top Products */}
                    <div className="dashboard-card">
                        <div className="card-header">
                            <h2>Top Products</h2>
                            <span className="card-subtitle">By Revenue</span>
                        </div>
                        <div className="card-content">
                            {topProducts.length === 0 ? (
                                <p className="text-muted text-center">No data yet</p>
                            ) : (
                                <div className="top-products-list">
                                    {topProducts.map((product, index) => (
                                        <div key={product.productId} className="top-product-item">
                                            <span className="rank">{index + 1}</span>
                                            <div className="product-info">
                                                <span className="product-name">{product.productName}</span>
                                                <span className="product-stats">
                                                    {product.totalQuantitySold} sold
                                                </span>
                                            </div>
                                            <span className="product-revenue">
                                                {formatPrice(product.totalRevenue)}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Low Stock Alert */}
                    <div className="dashboard-card">
                        <div className="card-header">
                            <h2>Low Stock Alert</h2>
                            <span className="card-subtitle">Less than 10 units</span>
                        </div>
                        <div className="card-content">
                            {lowStockProducts.length === 0 ? (
                                <p className="text-muted text-center">All stocked up! 🎉</p>
                            ) : (
                                <div className="low-stock-list">
                                    {lowStockProducts.map(product => (
                                        <div key={product.id} className="low-stock-item">
                                            <span className="product-name">{product.name}</span>
                                            <span className={`stock-count ${product.stockQuantity === 0 ? 'out' : 'low'}`}>
                                                {product.stockQuantity === 0 ? 'Out of Stock' : `${product.stockQuantity} left`}
                                            </span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Recent Orders */}
                    <div className="dashboard-card wide">
                        <div className="card-header">
                            <h2>Recent Orders</h2>
                            <Link to="/admin/orders" className="view-all-link">View All →</Link>
                        </div>
                        <div className="card-content">
                            {recentOrders.length === 0 ? (
                                <p className="text-muted text-center">No orders yet</p>
                            ) : (
                                <div className="orders-table">
                                    <div className="table-header">
                                        <span>Order ID</span>
                                        <span>Date</span>
                                        <span>Status</span>
                                        <span>Amount</span>
                                    </div>
                                    {recentOrders.map(order => (
                                        <div key={order.id} className="table-row">
                                            <span className="order-id">#{order.id}</span>
                                            <span className="order-date">{formatDate(order.createdAt)}</span>
                                            <span className={`badge ${getStatusBadge(order.status)}`}>
                                                {order.status}
                                            </span>
                                            <span className="order-amount">{formatPrice(order.totalAmount)}</span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
