import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Product, Category, Page } from '../types';
import { productsApi, categoriesApi } from '../api';
import './AdminProducts.css';

export function AdminProducts() {
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [pageInfo, setPageInfo] = useState<Omit<Page<Product>, 'content'> | null>(null);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [showModal, setShowModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState<Product | null>(null);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        stockQuantity: '',
        categoryId: '',
        active: true,
    });
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchProducts();
        fetchCategories();
    }, [page]);

    const fetchProducts = async () => {
        setLoading(true);
        try {
            const response = await productsApi.getAll({ page, size: 10 });
            setProducts(response.data.content);
            setPageInfo({
                totalElements: response.data.totalElements,
                totalPages: response.data.totalPages,
                size: response.data.size,
                number: response.data.number,
                first: response.data.first,
                last: response.data.last,
            });
        } catch (error) {
            console.error('Failed to fetch products:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await categoriesApi.getAll();
            setCategories(response.data);
        } catch (error) {
            console.error('Failed to fetch categories:', error);
        }
    };

    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0,
        }).format(price);
    };

    const openCreateModal = () => {
        setEditingProduct(null);
        setFormData({
            name: '',
            description: '',
            price: '',
            stockQuantity: '',
            categoryId: categories[0]?.id.toString() || '',
            active: true,
        });
        setError('');
        setShowModal(true);
    };

    const openEditModal = (product: Product) => {
        setEditingProduct(product);
        setFormData({
            name: product.name,
            description: product.description,
            price: product.price.toString(),
            stockQuantity: product.stockQuantity.toString(),
            categoryId: product.categoryId.toString(),
            active: product.active,
        });
        setError('');
        setShowModal(true);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSaving(true);

        try {
            const data = {
                name: formData.name,
                description: formData.description,
                price: parseFloat(formData.price),
                stockQuantity: parseInt(formData.stockQuantity),
                categoryId: parseInt(formData.categoryId),
                active: formData.active,
            };

            if (editingProduct) {
                await productsApi.update(editingProduct.id, data);
            } else {
                await productsApi.create(data);
            }

            setShowModal(false);
            fetchProducts();
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } } };
            setError(error.response?.data?.message || 'Failed to save product');
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (id: number) => {
        if (!window.confirm('Are you sure you want to delete this product?')) return;

        try {
            await productsApi.delete(id);
            fetchProducts();
        } catch (error) {
            console.error('Failed to delete product:', error);
        }
    };

    return (
        <div className="page admin-products">
            <div className="container">
                <div className="page-header">
                    <div>
                        <Link to="/admin" className="back-link">← Back to Dashboard</Link>
                        <h1 className="page-title">Manage Products</h1>
                        <p className="page-subtitle">{pageInfo?.totalElements || 0} products total</p>
                    </div>
                    <button className="btn btn-primary" onClick={openCreateModal}>
                        + Add Product
                    </button>
                </div>

                {loading ? (
                    <div className="loading-container">
                        <div className="spinner"></div>
                    </div>
                ) : (
                    <>
                        <div className="products-table">
                            <div className="table-header">
                                <span>Product</span>
                                <span>Category</span>
                                <span>Price</span>
                                <span>Stock</span>
                                <span>Status</span>
                                <span>Actions</span>
                            </div>
                            {products.map(product => (
                                <div key={product.id} className="table-row">
                                    <div className="product-cell">
                                        <span className="product-name">{product.name}</span>
                                    </div>
                                    <span className="category-cell">{product.categoryName}</span>
                                    <span className="price-cell">{formatPrice(product.price)}</span>
                                    <span className={`stock-cell ${product.stockQuantity <= 10 ? 'low' : ''}`}>
                                        {product.stockQuantity}
                                    </span>
                                    <span className={`badge ${product.active ? 'badge-success' : 'badge-danger'}`}>
                                        {product.active ? 'Active' : 'Inactive'}
                                    </span>
                                    <div className="actions-cell">
                                        <button
                                            className="btn btn-sm btn-secondary"
                                            onClick={() => openEditModal(product)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn btn-sm btn-danger"
                                            onClick={() => handleDelete(product.id)}
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>

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
                    </>
                )}

                {/* Modal */}
                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-header">
                                <h2>{editingProduct ? 'Edit Product' : 'Add Product'}</h2>
                                <button className="modal-close" onClick={() => setShowModal(false)}>✕</button>
                            </div>

                            {error && <div className="modal-error">{error}</div>}

                            <form onSubmit={handleSubmit} className="modal-form">
                                <div className="form-group">
                                    <label className="form-label">Name</label>
                                    <input
                                        type="text"
                                        className="form-input"
                                        value={formData.name}
                                        onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Description</label>
                                    <textarea
                                        className="form-input"
                                        rows={3}
                                        value={formData.description}
                                        onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                                    />
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label className="form-label">Price (₹)</label>
                                        <input
                                            type="number"
                                            className="form-input"
                                            value={formData.price}
                                            onChange={(e) => setFormData(prev => ({ ...prev, price: e.target.value }))}
                                            required
                                            min="0"
                                            step="0.01"
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">Stock Quantity</label>
                                        <input
                                            type="number"
                                            className="form-input"
                                            value={formData.stockQuantity}
                                            onChange={(e) => setFormData(prev => ({ ...prev, stockQuantity: e.target.value }))}
                                            required
                                            min="0"
                                        />
                                    </div>
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label className="form-label">Category</label>
                                        <select
                                            className="form-input"
                                            value={formData.categoryId}
                                            onChange={(e) => setFormData(prev => ({ ...prev, categoryId: e.target.value }))}
                                            required
                                        >
                                            {categories.map(cat => (
                                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">Status</label>
                                        <label className="checkbox-label">
                                            <input
                                                type="checkbox"
                                                checked={formData.active}
                                                onChange={(e) => setFormData(prev => ({ ...prev, active: e.target.checked }))}
                                            />
                                            Active
                                        </label>
                                    </div>
                                </div>

                                <div className="modal-actions">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary" disabled={saving}>
                                        {saving ? 'Saving...' : editingProduct ? 'Update' : 'Create'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
