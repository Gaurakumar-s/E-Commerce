import React from 'react';
import { Link } from 'react-router-dom';
import { Product } from '../types';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import './ProductCard.css';

interface ProductCardProps {
    product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
    const { isAuthenticated } = useAuth();
    const { addItem } = useCart();
    const [isAdding, setIsAdding] = React.useState(false);
    const [added, setAdded] = React.useState(false);

    const handleAddToCart = async (e: React.MouseEvent) => {
        e.preventDefault();
        e.stopPropagation();

        if (!isAuthenticated) {
            window.location.href = '/login';
            return;
        }

        setIsAdding(true);
        try {
            await addItem({ productId: product.id, quantity: 1 });
            setAdded(true);
            setTimeout(() => setAdded(false), 2000);
        } catch (error) {
            console.error('Failed to add to cart:', error);
        } finally {
            setIsAdding(false);
        }
    };

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    return (
        <Link to={`/products/${product.id}`} className="product-card">
            <div className="product-image">
                {product.imageUrl ? (
                    <img src={product.imageUrl} alt={product.name} />
                ) : (
                    <div className="product-image-placeholder">
                        📦
                    </div>
                )}
                {product.stockQuantity <= 5 && product.stockQuantity > 0 && (
                    <span className="stock-badge low">Only {product.stockQuantity} left!</span>
                )}
                {product.stockQuantity === 0 && (
                    <span className="stock-badge out">Out of Stock</span>
                )}
            </div>

            <div className="product-info">
                <span className="product-category">{product.categoryName}</span>
                <h3 className="product-name">{product.name}</h3>
                <p className="product-description">{product.description}</p>

                <div className="product-footer">
                    <span className="product-price">{formatPrice(product.price)}</span>
                    <button
                        className={`btn-add-cart ${added ? 'added' : ''}`}
                        onClick={handleAddToCart}
                        disabled={isAdding || product.stockQuantity === 0}
                    >
                        {isAdding ? '...' : added ? '✓' : '+ Cart'}
                    </button>
                </div>
            </div>
        </Link>
    );
}
