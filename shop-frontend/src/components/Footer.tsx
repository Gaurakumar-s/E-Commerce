import React from 'react';
import './Footer.css';

export function Footer() {
    return (
        <footer className="footer">
            <div className="container">
                <div className="footer-content">
                    <div className="footer-brand">
                        <span className="brand-icon">🛍️</span>
                        <span className="brand-name">ShopEase</span>
                        <p className="brand-tagline">Your one-stop online shopping destination</p>
                    </div>

                    <div className="footer-links">
                        <div className="footer-section">
                            <h4>Shop</h4>
                            <a href="/products">All Products</a>
                            <a href="/products?categoryId=1">Electronics</a>
                            <a href="/products?categoryId=2">Clothing</a>
                            <a href="/products?categoryId=3">Books</a>
                        </div>

                        <div className="footer-section">
                            <h4>Account</h4>
                            <a href="/login">Login</a>
                            <a href="/register">Register</a>
                            <a href="/orders">My Orders</a>
                            <a href="/cart">Cart</a>
                        </div>

                        <div className="footer-section">
                            <h4>Support</h4>
                            <a href="#">Help Center</a>
                            <a href="#">Contact Us</a>
                            <a href="#">Shipping Info</a>
                            <a href="#">Returns</a>
                        </div>
                    </div>
                </div>

                <div className="footer-bottom">
                    <p>&copy; 2026 ShopEase. All rights reserved.</p>
                    <p>Built with Spring Boot & React</p>
                </div>
            </div>
        </footer>
    );
}
