# Online Shopping Backend - Spring Boot

A production-grade REST API backend for an online shopping platform built with Spring Boot.

## Features

- **User Management**: Registration, login, JWT authentication with role-based access control
- **Product Catalog**: CRUD operations with pagination, sorting, filtering, and caching
- **Shopping Cart**: Add, update, remove items
- **Order Management**: Place orders, view history, cancel orders, admin order management
- **File Upload**: Product image upload with static file serving
- **Email Notifications**: Welcome emails and order confirmations via SMTP
- **Analytics**: Revenue reports, top products, low stock alerts, active users
- **Rate Limiting**: API rate limiting for login, orders, and general endpoints
- **Exception Handling**: Global exception handler with standardized error responses
- **API Documentation**: Swagger/OpenAPI documentation

## Tech Stack

- **Framework**: Spring Boot 3.3.3
- **Database**: MySQL/PostgreSQL (JPA/Hibernate)
- **Security**: Spring Security with JWT
- **Validation**: Jakarta Validation
- **Documentation**: Swagger/OpenAPI (springdoc-openapi)
- **Caching**: Spring Cache
- **Email**: Spring Mail (SMTP)

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ or PostgreSQL 12+
- SMTP server (Gmail SMTP recommended for testing)

## Setup Instructions

### 1. Database Setup

**MySQL:**
```sql
CREATE DATABASE shop_db;
```

**PostgreSQL:**
```sql
CREATE DATABASE shop_db;
```

### 2. Configuration

Update `src/main/resources/application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shop_db
    username: your_username
    password: your_password
```

For email configuration:
```yaml
spring:
  mail:
    username: your_email@gmail.com
    password: your_app_password
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Or run `ShopBackendApplication.java` directly from your IDE.

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token
- `GET /auth/me` - Get current user profile

### Products (Public - GET, Admin - POST/PUT/DELETE)
- `GET /api/products` - List products with pagination/filtering
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create product (Admin only)
- `PUT /api/products/{id}` - Update product (Admin only)
- `DELETE /api/products/{id}` - Delete product (Admin only)
- `POST /api/products/{id}/image` - Upload product image (Admin only)

### Categories
- `GET /api/categories` - List all categories
- `POST /api/categories` - Create category (Admin only)
- `PUT /api/categories/{id}` - Update category (Admin only)
- `DELETE /api/categories/{id}` - Delete category (Admin only)

### Cart (Authenticated Users)
- `GET /api/cart` - Get user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{itemId}` - Update cart item quantity
- `DELETE /api/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

### Orders (Authenticated Users)
- `POST /api/orders` - Place order from cart
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/my-orders` - Get user's orders
- `PUT /api/orders/{id}/cancel` - Cancel order
- `GET /api/orders` - Get all orders (Admin only)
- `PUT /api/orders/{id}/status` - Update order status (Admin only)

### Analytics (Admin only)
- `GET /api/analytics/top-products/revenue` - Top products by revenue
- `GET /api/analytics/top-products/quantity` - Top products by quantity
- `GET /api/analytics/revenue/daily` - Daily revenue report
- `GET /api/analytics/revenue/total` - Total revenue
- `GET /api/analytics/orders/total` - Total orders count
- `GET /api/analytics/products/low-stock` - Low stock products
- `GET /api/analytics/users/active` - Active users count

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Roles

- **CUSTOMER**: Can browse products, manage cart, place orders
- **ADMIN**: Full access including product/category management and analytics

## Rate Limiting

- Login endpoint: 5 requests per minute
- Order placement: 10 requests per minute
- General API: 100 requests per minute

## File Upload

Product images are stored in the `uploads/` directory and served at `/uploads/{filename}`.

## Email Configuration

For Gmail SMTP:
1. Enable 2-factor authentication
2. Generate an App Password
3. Use the app password in `application.yml`

## Project Structure

```
src/main/java/com/example/shop/
├── config/          # Configuration classes (Security, Swagger, etc.)
├── controller/      # REST controllers
├── service/         # Business logic
├── repository/      # Data access layer
├── model/           # JPA entities
├── dto/             # Data Transfer Objects
├── exception/       # Exception handling
└── util/            # Utility classes
```

## Testing the API

### 1. Register a User
```bash
POST /auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### 2. Login
```bash
POST /auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

### 3. Create an Admin User (via database)
```sql
-- Note: Password hash is BCrypt encoded
-- Default password: "admin123" (you'll need to generate hash)
INSERT INTO users (name, email, password_hash, created_at, updated_at)
VALUES ('Admin', 'admin@shop.com', '$2a$10$...', NOW(), NOW());

INSERT INTO user_roles (user_id, role) VALUES (1, 'ADMIN');
```

### 4. Use JWT Token
Include the token in subsequent requests:
```
Authorization: Bearer <token-from-login>
```

## Demo Checklist

- [x] User registration and login
- [x] JWT token generation and validation
- [x] Protected API endpoints
- [x] Product CRUD operations
- [x] Shopping cart management
- [x] Order placement and management
- [x] File upload (product images)
- [x] Email notifications
- [x] Analytics and reporting
- [x] Rate limiting
- [x] Swagger documentation

## Environment Variables

You can override configuration using environment variables:

- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT secret key (change in production!)
- `MAIL_HOST` - SMTP host
- `MAIL_PORT` - SMTP port
- `MAIL_USERNAME` - SMTP username
- `MAIL_PASSWORD` - SMTP password

## License

This project is for educational purposes.

## Contact

For questions or issues, please refer to the course documentation.
