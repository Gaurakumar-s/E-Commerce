import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, LoginRequest, RegisterRequest } from '../types';
import { authApi } from '../api';

interface AuthContextType {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isAdmin: boolean;
    isLoading: boolean;
    login: (data: LoginRequest) => Promise<void>;
    register: (data: RegisterRequest) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
    const [isLoading, setIsLoading] = useState(true);

    const isAuthenticated = !!token && !!user;
    const isAdmin = user?.roles?.includes('ADMIN') ?? false;

    useEffect(() => {
        const loadUser = async () => {
            if (token) {
                try {
                    const response = await authApi.me();
                    setUser(response.data);
                } catch {
                    // Token invalid, clear it
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    setToken(null);
                    setUser(null);
                }
            }
            setIsLoading(false);
        };
        loadUser();
    }, [token]);

    const login = async (data: LoginRequest) => {
        const response = await authApi.login(data);
        const newToken = response.data.accessToken;
        localStorage.setItem('token', newToken);
        setToken(newToken);

        // Fetch user data
        const userResponse = await authApi.me();
        setUser(userResponse.data);
        localStorage.setItem('user', JSON.stringify(userResponse.data));
    };

    const register = async (data: RegisterRequest) => {
        await authApi.register(data);
        // Auto login after registration
        await login({ email: data.email, password: data.password });
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{
            user,
            token,
            isAuthenticated,
            isAdmin,
            isLoading,
            login,
            register,
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
