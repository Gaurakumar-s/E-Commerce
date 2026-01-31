import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { Cart, AddCartItemRequest, UpdateCartItemRequest } from '../types';
import { cartApi } from '../api';
import { useAuth } from './AuthContext';

interface CartContextType {
    cart: Cart | null;
    isLoading: boolean;
    itemCount: number;
    addItem: (data: AddCartItemRequest) => Promise<void>;
    updateItem: (itemId: number, data: UpdateCartItemRequest) => Promise<void>;
    removeItem: (itemId: number) => Promise<void>;
    clearCart: () => Promise<void>;
    refreshCart: () => Promise<void>;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export function CartProvider({ children }: { children: ReactNode }) {
    const { isAuthenticated } = useAuth();
    const [cart, setCart] = useState<Cart | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    const itemCount = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) ?? 0;

    const refreshCart = useCallback(async () => {
        if (!isAuthenticated) {
            setCart(null);
            return;
        }

        setIsLoading(true);
        try {
            const response = await cartApi.get();
            setCart(response.data);
        } catch (error) {
            console.error('Failed to fetch cart:', error);
        } finally {
            setIsLoading(false);
        }
    }, [isAuthenticated]);

    useEffect(() => {
        refreshCart();
    }, [refreshCart]);

    const addItem = async (data: AddCartItemRequest) => {
        const response = await cartApi.addItem(data);
        setCart(response.data);
    };

    const updateItem = async (itemId: number, data: UpdateCartItemRequest) => {
        const response = await cartApi.updateItem(itemId, data);
        setCart(response.data);
    };

    const removeItem = async (itemId: number) => {
        const response = await cartApi.removeItem(itemId);
        setCart(response.data);
    };

    const clearCart = async () => {
        const response = await cartApi.clear();
        setCart(response.data);
    };

    return (
        <CartContext.Provider value={{
            cart,
            isLoading,
            itemCount,
            addItem,
            updateItem,
            removeItem,
            clearCart,
            refreshCart
        }}>
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    const context = useContext(CartContext);
    if (context === undefined) {
        throw new Error('useCart must be used within a CartProvider');
    }
    return context;
}
