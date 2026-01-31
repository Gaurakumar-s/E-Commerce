import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Product } from '../types';
import { productsApi } from '../api';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import './ProductDetailPage.css';

export function ProductDetailPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const { addItem } = useCart();

    const [product, setProduct] = useState<Product | null>(null);
    const [loading, setLoading] = useState(true);
    const [quantity, setQuantity] = useState(1);
    const [isAdding, setIsAdding] = useState(false);
    const [added, setAdded] = useState(false);

    useEffect(() => {
        const fetchProduct = async () => {
            if (!id) return;
            try {
                const response = await productsApi.getById(Number(id));
                setProduct(response.data);
            } catch (error) {
                console.error('Failed to fetch product:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchProduct();
    }, [id]);

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    const handleAddToCart = async () => {
        if (!isAuthenticated) {
            navigate('/login', { state: { from: `/products/${id}` } });
            return;
        }

        if (!product) return;

        setIsAdding(true);
        try {
            await addItem({ productId: product.id, quantity });
            setAdded(true);
            setTimeout(() => setAdded(false), 2000);
        } catch (error) {
            console.error('Failed to add to cart:', error);
        } finally {
            setIsAdding(false);
        }
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

    if (!product) {
        return (
            <div className="page">
                <div className="container">
                    <div className="empty-state">
                        <span className="empty-state-icon">404</span>
                        <h3 className="empty-state-title">Product Not Found</h3>
                        <button className="btn btn-primary" onClick={() => navigate('/products')}>
                            Browse Products
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="page product-detail-page">
            <div className="container">
                <button className="back-button" onClick={() => navigate(-1)}>
                    ← Back
                </button>

                <div className="product-detail">
                    <div className="product-image-section">
                        {product.imageUrl ? (
                            <img src={product.imageUrl} alt={product.name} className="product-main-image" />
                        ) : (
                            <div className="product-image-placeholder">📦</div>
                        )}
                    </div>

                    <div className="product-info-section">
                        <span className="product-category">{product.categoryName}</span>
                        <h1 className="product-title">{product.name}</h1>

                        <div className="product-price-section">
                            <span className="product-price">{formatPrice(product.price)}</span>
                            <span className={`stock-status ${product.stockQuantity > 0 ? 'in-stock' : 'out-of-stock'}`}>
                                {product.stockQuantity > 0
                                    ? `${product.stockQuantity} in stock`
                                    : 'Out of Stock'}
                            </span>
                        </div>

                        <div className="product-description">
                            <h3>Description</h3>
                            <p>{product.description}</p>
                        </div>

                        {product.stockQuantity > 0 && (
                            <div className="product-actions">
                                <div className="quantity-selector">
                                    <button
                                        className="qty-btn"
                                        onClick={() => setQuantity(prev => Math.max(1, prev - 1))}
                                        disabled={quantity <= 1}
                                    >
                                        −
                                    </button>
                                    <span className="qty-value">{quantity}</span>
                                    <button
                                        className="qty-btn"
                                        onClick={() => setQuantity(prev => Math.min(product.stockQuantity, prev + 1))}
                                        disabled={quantity >= product.stockQuantity}
                                    >
                                        +
                                    </button>
                                </div>

                                <button
                                    className={`btn btn-primary btn-lg add-to-cart-btn ${added ? 'added' : ''}`}
                                    onClick={handleAddToCart}
                                    disabled={isAdding}
                                >
                                    {isAdding ? 'Adding...' : added ? '✓ Added to Cart' : 'Add to Cart'}
                                </button>
                            </div>
                        )}

                        <div className="product-meta">
                            <div className="meta-item">
                                <span className="meta-label">SKU:</span>
                                <span className="meta-value">PROD-{product.id}</span>
                            </div>
                            <div className="meta-item">
                                <span className="meta-label">Category:</span>
                                <span className="meta-value">{product.categoryName}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
