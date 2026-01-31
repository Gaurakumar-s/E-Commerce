# Quick Start Guide

## Prerequisites Check

1. **Database**: Ensure MySQL/PostgreSQL is running and database `shop_db` is created
2. **Java**: Java 17+ installed
3. **Maven**: Maven 3.6+ installed (or use IDE)

## Step 1: Configure Database

Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    username: your_db_username
    password: your_db_password
```

## Step 2: Run the Application

```bash
mvn spring-boot:run
```

Or run `ShopBackendApplication.java` from your IDE.

## Step 3: Verify Application Started

Check console output for:
- "Started ShopBackendApplication"
- "Admin user created: admin@shop.com / admin123"
- "Demo customer created: customer@shop.com / customer123"
- "Sample products created"

## Step 4: Access Swagger UI

Open browser: http://localhost:8080/swagger-ui.html

## Quick Test Flow

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Save the `accessToken` from the response!**

### 3. Browse Products (No Auth Required)
```bash
curl http://localhost:8080/api/products
```

### 4. Get Your Profile
```bash
curl http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. Add Item to Cart
```bash
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### 6. View Cart
```bash
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 7. Place Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "credit_card"
  }'
```

### 8. View Your Orders
```bash
curl http://localhost:8080/api/orders/my-orders \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Admin Endpoints (Use admin@shop.com / admin123)

### Create Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Home & Garden",
    "active": true
  }'
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "description": "Product description",
    "price": 99.99,
    "stockQuantity": 50,
    "categoryId": 1,
    "active": true
  }'
```

### View Analytics
```bash
curl http://localhost:8080/api/analytics/revenue/total \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE"
```

## Pre-loaded Demo Data

The application automatically creates:

**Users:**
- Admin: `admin@shop.com` / `admin123`
- Customer: `customer@shop.com` / `customer123`

**Categories:**
- Electronics
- Clothing
- Books

**Products:**
- Laptop Pro 15 ($1299.99)
- Smartphone X ($799.99)
- Cotton T-Shirt ($19.99)
- Denim Jeans ($49.99)
- Programming Guide ($39.99)

## Troubleshooting

### Database Connection Error
- Check if MySQL/PostgreSQL is running
- Verify database `shop_db` exists
- Check credentials in `application.yml`

### Port Already in Use
- Change port in `application.yml`: `server.port: 8081`

### Email Not Sending
- Email is optional for basic functionality
- Configure SMTP in `application.yml` for email features
- Application will continue even if email fails

### JWT Token Expired
- Tokens expire after 1 hour (configurable in `application.yml`)
- Simply login again to get a new token

## Next Steps

1. Explore all endpoints in Swagger UI
2. Test rate limiting (try login 6 times quickly)
3. Upload a product image
4. Check analytics as admin
5. Review the README.md for complete documentation

## Demo Video Checklist

When recording your demo video, make sure to show:

1. ✅ Login & JWT token generation
2. ✅ Protected APIs (with and without token)
3. ✅ Database data (show products, orders in Swagger or DB)
4. ✅ File upload (upload product image)
5. ✅ Email sending (check email inbox)
6. ✅ Analytics APIs (as admin)
7. ✅ Rate limiting (show 429 error)
8. ✅ External integration (SMTP email)

Good luck with your project! 🚀
