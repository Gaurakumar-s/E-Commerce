import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Product, Category, Page } from '../types';
import { productsApi, categoriesApi } from '../api';
import { ProductCard } from '../components';
import './ProductsPage.css';

export function ProductsPage() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [products, setProducts] = useState<Product[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [pageInfo, setPageInfo] = useState<Omit<Page<Product>, 'content'> | null>(null);
    const [loading, setLoading] = useState(true);

    const categoryId = searchParams.get('categoryId') ? Number(searchParams.get('categoryId')) : undefined;
    const search = searchParams.get('search') || undefined;
    const page = Number(searchParams.get('page')) || 0;
    const minPrice = searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined;
    const maxPrice = searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined;

    useEffect(() => {
        const fetchProducts = async () => {
            setLoading(true);
            try {
                const response = await productsApi.getAll({
                    categoryId,
                    search,
                    minPrice,
                    maxPrice,
                    active: true,
                    page,
                    size: 12,
                });
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
        fetchProducts();
    }, [categoryId, search, page, minPrice, maxPrice]);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await categoriesApi.getAll();
                setCategories(response.data.filter(c => c.active));
            } catch (error) {
                console.error('Failed to fetch categories:', error);
            }
        };
        fetchCategories();
    }, []);

    const updateParams = (key: string, value: string | number | undefined) => {
        const newParams = new URLSearchParams(searchParams);
        if (value === undefined || value === '') {
            newParams.delete(key);
        } else {
            newParams.set(key, String(value));
        }
        if (key !== 'page') {
            newParams.delete('page');
        }
        setSearchParams(newParams);
    };

    const clearFilters = () => {
        setSearchParams({});
    };

    return (
        <div className="page products-page">
            <div className="container">
                <div className="products-layout">
                    {/* Sidebar */}
                    <aside className="products-sidebar">
                        <div className="filter-section">
                            <h3>Categories</h3>
                            <div className="filter-options">
                                <button
                                    className={`filter-option ${!categoryId ? 'active' : ''}`}
                                    onClick={() => updateParams('categoryId', undefined)}
                                >
                                    All Categories
                                </button>
                                {categories.map(cat => (
                                    <button
                                        key={cat.id}
                                        className={`filter-option ${categoryId === cat.id ? 'active' : ''}`}
                                        onClick={() => updateParams('categoryId', cat.id)}
                                    >
                                        {cat.name}
                                    </button>
                                ))}
                            </div>
                        </div>

                        <div className="filter-section">
                            <h3>Price Range</h3>
                            <div className="price-inputs">
                                <input
                                    type="number"
                                    placeholder="Min"
                                    className="form-input"
                                    value={minPrice || ''}
                                    onChange={(e) => updateParams('minPrice', e.target.value || undefined)}
                                />
                                <span>to</span>
                                <input
                                    type="number"
                                    placeholder="Max"
                                    className="form-input"
                                    value={maxPrice || ''}
                                    onChange={(e) => updateParams('maxPrice', e.target.value || undefined)}
                                />
                            </div>
                        </div>

                        <button className="btn btn-secondary w-full" onClick={clearFilters}>
                            Clear Filters
                        </button>
                    </aside>

                    {/* Main Content */}
                    <main className="products-main">
                        <div className="products-header">
                            <div className="products-info">
                                <h1>
                                    {categoryId
                                        ? categories.find(c => c.id === categoryId)?.name
                                        : search
                                            ? `Results for "${search}"`
                                            : 'All Products'}
                                </h1>
                                {pageInfo && (
                                    <p>{pageInfo.totalElements} products found</p>
                                )}
                            </div>
                        </div>

                        {loading ? (
                            <div className="loading-container">
                                <div className="spinner"></div>
                            </div>
                        ) : products.length === 0 ? (
                            <div className="empty-state">
                                <span className="empty-state-icon">🔍</span>
                                <h3 className="empty-state-title">No products found</h3>
                                <p>Try adjusting your filters or search terms</p>
                            </div>
                        ) : (
                            <>
                                <div className="products-grid">
                                    {products.map(product => (
                                        <ProductCard key={product.id} product={product} />
                                    ))}
                                </div>

                                {/* Pagination */}
                                {pageInfo && pageInfo.totalPages > 1 && (
                                    <div className="pagination">
                                        <button
                                            className="btn btn-secondary"
                                            disabled={pageInfo.first}
                                            onClick={() => updateParams('page', page - 1)}
                                        >
                                            ← Previous
                                        </button>
                                        <span className="pagination-info">
                                            Page {pageInfo.number + 1} of {pageInfo.totalPages}
                                        </span>
                                        <button
                                            className="btn btn-secondary"
                                            disabled={pageInfo.last}
                                            onClick={() => updateParams('page', page + 1)}
                                        >
                                            Next →
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </main>
                </div>
            </div>
        </div>
    );
}
