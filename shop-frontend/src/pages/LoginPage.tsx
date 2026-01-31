import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { AxiosError } from 'axios';
import { ApiError } from '../types';
import './AuthPages.css';

export function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const from = (location.state as { from?: string })?.from || '/';

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setIsLoading(true);

        try {
            await login({ email, password });
            navigate(from, { replace: true });
        } catch (err) {
            const axiosError = err as AxiosError<ApiError>;
            setError(axiosError.response?.data?.message || 'Login failed. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <span className="auth-icon">👋</span>
                        <h1>Welcome Back</h1>
                        <p>Sign in to your account to continue</p>
                    </div>

                    {error && <div className="auth-error">{error}</div>}

                    <form onSubmit={handleSubmit} className="auth-form">
                        <div className="form-group">
                            <label className="form-label">Email</label>
                            <input
                                type="email"
                                className="form-input"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Password</label>
                            <input
                                type="password"
                                className="form-input"
                                placeholder="Enter your password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Signing in...' : 'Sign In'}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <p>
                            Don't have an account?{' '}
                            <Link to="/register">Create one</Link>
                        </p>
                    </div>

                    <div className="demo-accounts">
                        <p>Demo Accounts:</p>
                        <div className="demo-account">
                            <strong>Admin:</strong> admin@shop.com / admin123
                        </div>
                        <div className="demo-account">
                            <strong>Customer:</strong> customer@shop.com / customer123
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
