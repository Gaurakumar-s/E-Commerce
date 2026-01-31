import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Product, Category } from '../types';
import { productsApi, categoriesApi } from '../api';
import { ProductCard } from '../components';
import './HomePage.css';

export function HomePage() {
    const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [productsRes, categoriesRes] = await Promise.all([
                    productsApi.getAll({ size: 8, active: true }),
                    categoriesApi.getAll(),
                ]);
                setFeaturedProducts(productsRes.data.content);
                setCategories(categoriesRes.data.filter(c => c.active));
            } catch (error) {
                console.error('Failed to fetch data:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    return (
        <div className="home-page">
            {/* Hero Section */}
            <section className="hero">
                <div className="hero-bg"></div>
                <div className="container hero-content">
                    <h1 className="hero-title">
                        Discover Amazing <span className="gradient-text">Products</span>
                    </h1>
                    <p className="hero-subtitle">
                        Shop the latest trends with fast delivery and secure payments
                    </p>
                    <div className="hero-actions">
                        <Link to="/products" className="btn btn-primary btn-lg">
                            Shop Now
                        </Link>
                        <Link to="/register" className="btn btn-secondary btn-lg">
                            Create Account
                        </Link>
                    </div>

                    <div className="hero-stats">
                        <div className="stat">
                            <span className="stat-value">500+</span>
                            <span className="stat-label">Products</span>
                        </div>
                        <div className="stat">
                            <span className="stat-value">10K+</span>
                            <span className="stat-label">Customers</span>
                        </div>
                        <div className="stat">
                            <span className="stat-value">24/7</span>
                            <span className="stat-label">Support</span>
                        </div>
                    </div>
                </div>
            </section>

            {/* Categories Section */}
            <section className="section categories-section">
                <div className="container">
                    <div className="section-header">
                        <h2>Shop by Category</h2>
                        <p>Browse our wide selection of products</p>
                    </div>

                    <div className="categories-grid">
                        {categories.map(category => (
                            <Link
                                key={category.id}
                                to={`/products?categoryId=${category.id}`}
                                className="category-card"
                            >
                                <span className="category-icon">
                                    {category.name === 'Electronics' && '📱'}
                                    {category.name === 'Clothing' && '👕'}
                                    {category.name === 'Books' && '📚'}
                                    {!['Electronics', 'Clothing', 'Books'].includes(category.name) && '📦'}
                                </span>
                                <span className="category-name">{category.name}</span>
                            </Link>
                        ))}
                    </div>
                </div>
            </section>

            {/* Featured Products Section */}
            <section className="section products-section">
                <div className="container">
                    <div className="section-header">
                        <h2>Featured Products</h2>
                        <Link to="/products" className="view-all-link">View All →</Link>
                    </div>

                    {loading ? (
                        <div className="loading-container">
                            <div className="spinner"></div>
                        </div>
                    ) : (
                        <div className="grid grid-4">
                            {featuredProducts.map(product => (
                                <ProductCard key={product.id} product={product} />
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* Features Section */}
            <section className="section features-section">
                <div className="container">
                    <div className="features-grid">
                        <div className="feature-card">
                            <span className="feature-icon">🚚</span>
                            <h3>Free Shipping</h3>
                            <p>On orders over ₹500</p>
                        </div>
                        <div className="feature-card">
                            <span className="feature-icon">🔒</span>
                            <h3>Secure Payment</h3>
                            <p>100% secure checkout</p>
                        </div>
                        <div className="feature-card">
                            <span className="feature-icon">↩️</span>
                            <h3>Easy Returns</h3>
                            <p>30-day return policy</p>
                        </div>
                        <div className="feature-card">
                            <span className="feature-icon">💬</span>
                            <h3>24/7 Support</h3>
                            <p>Always here to help</p>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}
