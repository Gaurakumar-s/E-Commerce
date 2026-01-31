# Shop Frontend

React + TypeScript frontend for the Online Shopping Backend.

## Tech Stack

- **React 18** with TypeScript
- **Vite** for fast development
- **React Router v6** for navigation
- **Axios** for API calls
- Modern CSS with dark theme, gradients, and animations

## Features

- 🛒 **Shopping Cart** - Add, update, remove items
- 📦 **Product Catalog** - Search, filter, pagination
- 🔐 **Authentication** - Login, register with JWT
- 📋 **Orders** - Place orders, view history
- 📊 **Admin Dashboard** - Analytics, manage products
- 🎨 **Modern UI** - Dark theme, glassmorphism, smooth animations

## Setup

### 1. Install Dependencies

```bash
cd shop-frontend
npm install
```

### 2. Start Development Server

```bash
npm run dev
```

The app runs on `http://localhost:3000` and proxies API calls to `http://localhost:8080`.

### 3. Make sure the backend is running

The backend should be running on port 8080. Start it with:

```bash
cd shop-backend
mvn spring-boot:run
```

## Demo Accounts

- **Admin**: `admin@shop.com` / `admin123`
- **Customer**: `customer@shop.com` / `customer123`

## Project Structure

```
src/
├── api/           # Axios client and API services
├── components/    # Reusable UI components
├── contexts/      # React contexts (Auth, Cart)
├── pages/         # Page components
├── types/         # TypeScript interfaces
├── App.tsx        # Main app with routes
├── main.tsx       # Entry point
└── index.css      # Global styles
```

## Pages

| Route | Description | Access |
|-------|-------------|--------|
| `/` | Home page | Public |
| `/products` | Product listing | Public |
| `/products/:id` | Product details | Public |
| `/login` | Login page | Public |
| `/register` | Register page | Public |
| `/cart` | Shopping cart | Auth |
| `/orders` | Order history | Auth |
| `/orders/:id` | Order details | Auth |
| `/admin` | Admin dashboard | Admin |
| `/admin/products` | Manage products | Admin |

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
