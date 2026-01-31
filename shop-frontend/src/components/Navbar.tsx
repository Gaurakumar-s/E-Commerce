import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useCart } from '../contexts/CartContext';
import './Navbar.css';

export function Navbar() {
    const { isAuthenticated, isAdmin, user, logout } = useAuth();
    const { itemCount } = useCart();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className="navbar">
            <div className="container navbar-container">
                <Link to="/" className="navbar-brand">
                    <span className="brand-icon">🛍️</span>
                    <span className="brand-text">ShopEase</span>
                </Link>

                <div className="navbar-search">
                    <input
                        type="text"
                        placeholder="Search products..."
                        className="search-input"
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                const target = e.target as HTMLInputElement;
                                navigate(`/products?search=${encodeURIComponent(target.value)}`);
                            }
                        }}
                    />
                    <span className="search-icon">🔍</span>
                </div>

                <div className="navbar-links">
                    <Link to="/products" className="nav-link">Products</Link>

                    {isAuthenticated ? (
                        <>
                            <Link to="/cart" className="nav-link cart-link">
                                🛒 Cart
                                {itemCount > 0 && <span className="cart-badge">{itemCount}</span>}
                            </Link>

                            <Link to="/orders" className="nav-link">Orders</Link>

                            {isAdmin && (
                                <Link to="/admin" className="nav-link admin-link">Dashboard</Link>
                            )}

                            <div className="user-menu">
                                <button className="user-button">
                                    <span className="user-avatar">👤</span>
                                    <span className="user-name">{user?.name?.split(' ')[0]}</span>
                                </button>
                                <div className="user-dropdown">
                                    <div className="dropdown-header">
                                        <strong>{user?.name}</strong>
                                        <small>{user?.email}</small>
                                    </div>
                                    <div className="dropdown-divider"></div>
                                    <Link to="/orders" className="dropdown-item">My Orders</Link>
                                    {isAdmin && <Link to="/admin" className="dropdown-item">Admin Dashboard</Link>}
                                    <div className="dropdown-divider"></div>
                                    <button onClick={handleLogout} className="dropdown-item logout-btn">
                                        Logout
                                    </button>
                                </div>
                            </div>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link">Login</Link>
                            <Link to="/register" className="btn btn-primary btn-sm">Sign Up</Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}
