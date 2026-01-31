import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { ordersApi } from '../api';
import './CartPage.css';

export function CartPage() {
    const { cart, isLoading, updateItem, removeItem, clearCart, refreshCart } = useCart();
    const navigate = useNavigate();
    const [isCheckingOut, setIsCheckingOut] = useState(false);
    const [error, setError] = useState('');

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    const handleQuantityChange = async (itemId: number, newQuantity: number) => {
        if (newQuantity < 1) return;
        try {
            await updateItem(itemId, { quantity: newQuantity });
        } catch (error) {
            console.error('Failed to update quantity:', error);
        }
    };

    const handleRemove = async (itemId: number) => {
        try {
            await removeItem(itemId);
        } catch (error) {
            console.error('Failed to remove item:', error);
        }
    };

    const handleCheckout = async () => {
        setError('');
        setIsCheckingOut(true);
        try {
            const response = await ordersApi.place({});
            await refreshCart();
            navigate(`/orders/${response.data.id}`, { state: { newOrder: true } });
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } };
            setError(error.response?.data?.message || 'Failed to place order. Please try again.');
        } finally {
            setIsCheckingOut(false);
        }
    };

    if (isLoading) {
        return (
            <div className="page">
                <div className="loading-container">
                    <div className="spinner"></div>
                </div>
            </div>
        );
    }

    if (!cart || cart.items.length === 0) {
        return (
            <div className="page cart-page">
                <div className="container">
                    <div className="empty-state">
                        <span className="empty-state-icon">🛒</span>
                        <h3 className="empty-state-title">Your cart is empty</h3>
                        <p>Looks like you haven't added any items yet</p>
                        <Link to="/products" className="btn btn-primary mt-3">
                            Start Shopping
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="page cart-page">
            <div className="container">
                <div className="page-header">
                    <h1 className="page-title">Shopping Cart</h1>
                    <p className="page-subtitle">{cart.items.length} items in your cart</p>
                </div>

                {error && <div className="cart-error">{error}</div>}

                <div className="cart-layout">
                    <div className="cart-items">
                        {cart.items.map(item => (
                            <div key={item.id} className="cart-item">
                                <div className="cart-item-image">📦</div>

                                <div className="cart-item-info">
                                    <Link to={`/products/${item.productId}`} className="cart-item-name">
                                        {item.productName}
                                    </Link>
                                    <span className="cart-item-price">{formatPrice(item.priceAtAddTime)} each</span>
                                </div>

                                <div className="cart-item-quantity">
                                    <button
                                        className="qty-btn"
                                        onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                                        disabled={item.quantity <= 1}
                                    >
                                        −
                                    </button>
                                    <span className="qty-value">{item.quantity}</span>
                                    <button
                                        className="qty-btn"
                                        onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                                    >
                                        +
                                    </button>
                                </div>

                                <div className="cart-item-total">
                                    {formatPrice(item.lineTotal)}
                                </div>

                                <button
                                    className="cart-item-remove"
                                    onClick={() => handleRemove(item.id)}
                                    title="Remove item"
                                >
                                    ✕
                                </button>
                            </div>
                        ))}

                        <div className="cart-actions">
                            <button className="btn btn-ghost" onClick={() => clearCart()}>
                                Clear Cart
                            </button>
                            <Link to="/products" className="btn btn-secondary">
                                Continue Shopping
                            </Link>
                        </div>
                    </div>

                    <div className="cart-summary">
                        <h3>Order Summary</h3>

                        <div className="summary-row">
                            <span>Subtotal</span>
                            <span>{formatPrice(cart.totalAmount)}</span>
                        </div>
                        <div className="summary-row">
                            <span>Shipping</span>
                            <span className="free-shipping">Free</span>
                        </div>
                        <div className="summary-row total">
                            <span>Total</span>
                            <span>{formatPrice(cart.totalAmount)}</span>
                        </div>

                        <button
                            className="btn btn-primary w-full checkout-btn"
                            onClick={handleCheckout}
                            disabled={isCheckingOut}
                        >
                            {isCheckingOut ? 'Placing Order...' : 'Proceed to Checkout'}
                        </button>

                        <div className="secure-checkout">
                            🔒 Secure checkout
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
