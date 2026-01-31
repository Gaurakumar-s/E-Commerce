import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth, CartProvider } from './contexts';
import { Navbar, Footer } from './components';
import {
    HomePage,
    LoginPage,
    RegisterPage,
    ProductsPage,
    ProductDetailPage,
    CartPage,
    OrdersPage,
    OrderDetailPage,
    AdminDashboard,
    AdminProducts,
} from './pages';
import './index.css';

// Protected Route Component
function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
}

// Admin Route Component
function AdminRoute({ children }: { children: React.ReactNode }) {
    const { isAuthenticated, isAdmin, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    if (!isAdmin) {
        return <Navigate to="/" replace />;
    }

    return <>{children}</>;
}

// Layout Component
function Layout({ children }: { children: React.ReactNode }) {
    return (
        <>
            <Navbar />
            {children}
            <Footer />
        </>
    );
}

// App Routes
function AppRoutes() {
    return (
        <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Layout><HomePage /></Layout>} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/products" element={<Layout><ProductsPage /></Layout>} />
            <Route path="/products/:id" element={<Layout><ProductDetailPage /></Layout>} />

            {/* Protected Routes */}
            <Route path="/cart" element={
                <ProtectedRoute>
                    <Layout><CartPage /></Layout>
                </ProtectedRoute>
            } />
            <Route path="/orders" element={
                <ProtectedRoute>
                    <Layout><OrdersPage /></Layout>
                </ProtectedRoute>
            } />
            <Route path="/orders/:id" element={
                <ProtectedRoute>
                    <Layout><OrderDetailPage /></Layout>
                </ProtectedRoute>
            } />

            {/* Admin Routes */}
            <Route path="/admin" element={
                <AdminRoute>
                    <Layout><AdminDashboard /></Layout>
                </AdminRoute>
            } />
            <Route path="/admin/products" element={
                <AdminRoute>
                    <Layout><AdminProducts /></Layout>
                </AdminRoute>
            } />

            {/* 404 */}
            <Route path="*" element={
                <Layout>
                    <div className="page">
                        <div className="container">
                            <div className="empty-state">
                                <span className="empty-state-icon">404</span>
                                <h3 className="empty-state-title">Page Not Found</h3>
                                <p>The page you're looking for doesn't exist.</p>
                                <a href="/" className="btn btn-primary mt-3">Go Home</a>
                            </div>
                        </div>
                    </div>
                </Layout>
            } />
        </Routes>
    );
}

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <CartProvider>
                    <AppRoutes />
                </CartProvider>
            </AuthProvider>
        </BrowserRouter>
    );
}

export default App;
