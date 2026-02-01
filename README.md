# 🛒 Online Shopping Backend



A production-ready REST API for e-commerce applications with JWT authentication, cart management, order processing, and admin analytics.

## ✨ Features

- 🔐 **JWT Authentication** - Secure, stateless auth with role-based access
- 📦 **Product Management** - CRUD operations with image upload and filtering
- 🛒 **Shopping Cart** - Real-time cart with stock validation
- 📋 **Order Processing** - Complete order lifecycle with email notifications
- 📊 **Admin Analytics** - Revenue reports, top products, and inventory alerts
- ⚡ **Rate Limiting** - Protect APIs from abuse
- 🔒 **Security** - BCrypt passwords, CORS, input validation

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (or use H2 for dev)

### Installation

```bash
# Clone repository
git clone https://github.com/yourusername/shop-backend.git
cd shop-backend

# Configure database (edit application.yml)
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shop_db
    username: shopuser
    password: your_password

# Run application
mvn spring-boot:run
```

**Access Swagger UI**: http://localhost:8080/swagger-ui.html

### Demo Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@shop.com | admin123 | Admin |
| customer@shop.com | customer123 | Customer |

## 🏗️ Architecture

```
┌─────────────────────┐
│   Controllers       │  ← REST API Layer
├─────────────────────┤
│   Services          │  ← Business Logic
├─────────────────────┤
│   Repositories      │  ← Data Access (JPA)
├─────────────────────┤
│   MySQL Database    │  ← Persistence
└─────────────────────┘
```

**Tech Stack**: Spring Boot 3.3, Spring Security, JWT, JPA/Hibernate, MySQL, Swagger

## 📡 Key Endpoints

### Authentication
```bash
POST /auth/register  # Register new user
POST /auth/login     # Get JWT token
GET  /auth/me        # Current user profile
```

### Products
```bash
GET    /api/products              # Browse products (public)
GET    /api/products/{id}         # Product details
POST   /api/products              # Create (admin)
PUT    /api/products/{id}         # Update (admin)
DELETE /api/products/{id}         # Delete (admin)
```

### Shopping Cart
```bash
GET    /api/cart             # View cart
POST   /api/cart/items       # Add item
PUT    /api/cart/items/{id}  # Update quantity
DELETE /api/cart/items/{id}  # Remove item
```

### Orders
```bash
POST /api/orders                # Place order
GET  /api/orders/my-orders      # Order history
PUT  /api/orders/{id}/cancel    # Cancel order
GET  /api/orders                # All orders (admin)
```

### Analytics (Admin)
```bash
GET /api/analytics/revenue/total           # Total revenue
GET /api/analytics/top-products/revenue    # Best sellers
GET /api/analytics/products/low-stock      # Inventory alerts
```

**Total**: 31 endpoints | [Full API Docs →](http://localhost:8080/swagger-ui.html)

## 🔐 Authentication

### 1. Login to get JWT token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@shop.com","password":"admin123"}'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 2. Use token in requests
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🗄️ Database Schema

```
users ──┬── user_roles
        ├── carts ─── cart_items ─── products ─── categories
        └── orders ── order_items ─┘
```

**8 Tables**: users, user_roles, products, categories, carts, cart_items, orders, order_items

<details>
<summary>📊 View Full ERD</summary>

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│    USERS    │────1:M──│    ORDERS    │────1:M──│ ORDER_ITEMS  │
├─────────────┤         ├──────────────┤         ├──────────────┤
│ id          │         │ id           │         │ id           │
│ email       │         │ user_id (FK) │         │ order_id (FK)│
│ password    │         │ total_amount │         │ product_id   │
│ name        │         │ status       │         │ quantity     │
│ address     │         │ payment_ref  │         │ price_each   │
└─────────────┘         └──────────────┘         └──────────────┘
       │                                                  │
       │                                                  │
       │                ┌──────────────┐                 │
       └────1:1─────────│    CARTS     │                 │
                        ├──────────────┤                 │
                        │ id           │                 │
                        │ user_id (FK) │                 │
                        └──────────────┘                 │
                               │                         │
                               │                         │
                            1:M│                         │
                               │                         │
                        ┌──────────────┐                 │
                        │  CART_ITEMS  │                 │
                        ├──────────────┤                 │
                        │ id           │                 │
                        │ cart_id (FK) │                 │
                        │ product_id ──┼─────────────────┘
                        │ quantity     │         │
                        └──────────────┘         │
                                                 │
                        ┌──────────────┐         │
                        │   PRODUCTS   │◄────────┘
                        ├──────────────┤
                        │ id           │
                        │ name         │
                        │ price        │
                        │ stock_qty    │
                        │ category_id  │
                        └──────┬───────┘
                               │
                            M:1│
                               │
                        ┌──────────────┐
                        │  CATEGORIES  │
                        ├──────────────┤
                        │ id           │
                        │ name         │
                        │ parent_id    │
                        └──────────────┘
```
</details>

## 🔄 Flowcharts

### Authentication Flow
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ POST /auth/register
       │ {email, password}
       ▼
┌─────────────────────────────────┐
│     Registration Process         │
├─────────────────────────────────┤
│ 1. Validate email format        │
│ 2. Check email not exists       │
│ 3. Hash password (BCrypt)       │
│ 4. Save user to database        │
│ 5. Assign CUSTOMER role         │
│ 6. Send welcome email           │
└──────┬──────────────────────────┘
       │
       │ 201 Created
       │ {id, email, roles}
       ▼
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ POST /auth/login
       │ {email, password}
       ▼
┌─────────────────────────────────┐
│       Login Process              │
├─────────────────────────────────┤
│ 1. Find user by email           │
│ 2. Verify password (BCrypt)     │
│ 3. Generate JWT token           │
│    - Subject: email             │
│    - Expiry: 1 hour             │
│    - Sign with secret           │
└──────┬──────────────────────────┘
       │
       │ 200 OK
       │ {accessToken, tokenType}
       ▼
┌─────────────┐
│   Client    │ Stores token
└─────────────┘
```

### Protected Request Flow
```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ GET /api/cart
       │ Header: Authorization: Bearer <token>
       ▼
┌────────────────────────────────────┐
│     Rate Limiting Filter           │
│  • Check request count             │
│  • 100 requests/min limit          │
│  • Return 429 if exceeded          │
└──────┬─────────────────────────────┘
       │ ✓ Allowed
       ▼
┌────────────────────────────────────┐
│    JWT Authentication Filter       │
│  • Extract token from header       │
│  • Validate signature              │
│  • Check expiration                │
│  • Load user from database         │
└──────┬─────────────────────────────┘
       │ ✓ Valid token
       ▼
┌────────────────────────────────────┐
│      Spring Security               │
│  • Check user authenticated        │
│  • Verify required role            │
│  • Authorize request               │
└──────┬─────────────────────────────┘
       │ ✓ Authorized
       ▼
┌────────────────────────────────────┐
│         Controller                 │
│  • Process request                 │
│  • Call service layer              │
│  • Return response                 │
└──────┬─────────────────────────────┘
       │
       │ 200 OK
       │ {cart data}
       ▼
┌─────────────┐
│   Client    │
└─────────────┘
```

### Shopping Cart Flow
```
┌─────────────┐
│   Customer  │
└──────┬──────┘
       │
       │ 1. Browse Products
       ▼
┌─────────────────────────────────┐
│  GET /api/products              │
│  • Filter by category           │
│  • Search by keyword            │
│  • Sort by price                │
└──────┬──────────────────────────┘
       │
       │ 2. Select Product
       ▼
┌─────────────────────────────────┐
│  POST /api/cart/items           │
│  {productId: 1, quantity: 2}    │
│                                 │
│  Process:                       │
│  ├─ Validate product exists     │
│  ├─ Check stock available       │
│  ├─ Add to cart                 │
│  └─ Calculate total             │
└──────┬──────────────────────────┘
       │
       │ 3. Update Quantity
       ▼
┌─────────────────────────────────┐
│  PUT /api/cart/items/{id}       │
│  {quantity: 3}                  │
│                                 │
│  Process:                       │
│  ├─ Validate new quantity       │
│  ├─ Check stock available       │
│  └─ Update total                │
└──────┬──────────────────────────┘
       │
       │ 4. Review Cart
       ▼
┌─────────────────────────────────┐
│  GET /api/cart                  │
│                                 │
│  Returns:                       │
│  ├─ All cart items              │
│  ├─ Individual line totals      │
│  └─ Grand total                 │
└──────┬──────────────────────────┘
       │
       │ 5. Checkout
       ▼
┌─────────────────────────────────┐
│  POST /api/orders               │
│  (See Order Processing Flow)    │
└─────────────────────────────────┘
```

### Order Processing Flow
```
┌─────────────┐
│  Customer   │
└──────┬──────┘
       │
       │ POST /api/orders
       │ {paymentMethod: "credit_card"}
       ▼
┌────────────────────────────────────────┐
│         Order Processing                │
│        (@Transactional)                 │
├────────────────────────────────────────┤
│                                         │
│  Step 1: Validate Cart                 │
│  ├─ Check cart exists                  │
│  ├─ Verify cart has items              │
│  └─ If empty → throw exception         │
│                                         │
│  Step 2: Validate Stock                │
│  ├─ For each cart item:                │
│  │   ├─ Check product active           │
│  │   ├─ Verify stock >= quantity       │
│  │   └─ If not → rollback transaction  │
│                                         │
│  Step 3: Create Order                  │
│  ├─ Generate order ID                  │
│  ├─ Set status = CREATED               │
│  ├─ Set payment = PAID (mock)          │
│  ├─ Generate payment reference         │
│  └─ Calculate total amount             │
│                                         │
│  Step 4: Create Order Items            │
│  ├─ For each cart item:                │
│  │   ├─ Copy product details           │
│  │   ├─ Snapshot name & price          │
│  │   ├─ Set quantity                   │
│  │   └─ Calculate subtotal             │
│                                         │
│  Step 5: Update Stock                  │
│  ├─ For each cart item:                │
│  │   ├─ Reduce product stock           │
│  │   └─ Save product                   │
│                                         │
│  Step 6: Clear Cart                    │
│  ├─ Remove all cart items              │
│  └─ Save empty cart                    │
│                                         │
│  Step 7: Send Email                    │
│  └─ Order confirmation (async)         │
│                                         │
└────────────┬───────────────────────────┘
             │
             │ 201 Created
             │ {orderId, total, status, items}
             ▼
      ┌─────────────┐
      │  Customer   │
      └─────────────┘
      
      
If ANY step fails:
├─ Entire transaction rolls back
├─ No order created
├─ Stock unchanged
└─ Cart remains intact
```

### Stock Management Flow
```
Product Stock Lifecycle:

┌───────────────────┐
│ Initial Stock: 50 │
└─────────┬─────────┘
          │
          │ Customer adds 5 units to cart
          ▼
┌───────────────────┐
│ Stock: 50         │ ← No change (just reserved in cart)
└─────────┬─────────┘
          │
          │ Customer places order
          ▼
┌───────────────────┐
│ Stock: 45         │ ← Reduced by 5
└─────────┬─────────┘
          │
          ├─────────────────┬─────────────────┐
          │                 │                 │
          ▼                 ▼                 ▼
   ┌──────────┐      ┌──────────┐     ┌──────────┐
   │ Confirmed│      │Cancelled │     │Delivered │
   └──────────┘      └────┬─────┘     └──────────┘
   Stock: 45               │           Stock: 45
                           │
                           │ Restore stock
                           ▼
                    ┌──────────┐
                    │Stock: 50 │ ← Restored
                    └──────────┘

Low Stock Alert:
└─> Triggers when stock ≤ 10 units
```

### Order Status Lifecycle
```
┌──────────┐
│ CREATED  │ ← Order placed, payment processed
└────┬─────┘
     │
     │ Admin action: Confirm order
     ▼
┌──────────┐
│CONFIRMED │ ← Order accepted, preparing for shipment
└────┬─────┘
     │
     │ Admin action: Ship order
     ▼
┌──────────┐
│ SHIPPED  │ ← Order dispatched, in transit
└────┬─────┘
     │
     │ Admin action: Mark delivered
     ▼
┌──────────┐
│DELIVERED │ ← Order completed successfully
└──────────┘

Can be cancelled:
┌──────────┐
│ CREATED  │
└────┬─────┘
     │ Customer/Admin action
     ▼
┌──────────┐
│CANCELLED │ ← Stock restored, order closed
└──────────┘

Cannot be cancelled:
- DELIVERED (already received)
- Already CANCELLED
```

### Admin Analytics Flow
```
┌─────────────┐
│    Admin    │
└──────┬──────┘
       │
       │ Request: Top Products
       ▼
┌─────────────────────────────────┐
│ GET /api/analytics/             │
│     top-products/revenue        │
│                                 │
│ Process:                        │
│ ├─ Filter: PAID orders only     │
│ ├─ Group by: Product ID         │
│ ├─ Calculate: Total revenue     │
│ ├─ Sort: Descending by revenue  │
│ └─ Limit: Top N products        │
└──────┬──────────────────────────┘
       │
       │ Response
       ▼
┌─────────────────────────────────┐
│ [                               │
│   {                             │
│     productId: 1,               │
│     name: "Laptop Pro",         │
│     totalRevenue: 52999.50,     │
│     unitsSold: 42               │
│   },                            │
│   ...                           │
│ ]                               │
└─────────────────────────────────┘
       │
       │ Display on Dashboard
       ▼
┌─────────────┐
│    Admin    │
└─────────────┘
```

### Complete E-Commerce Flow
```
┌──────────────────────────────────────────────────────────────┐
│                     Customer Journey                          │
└──────────────────────────────────────────────────────────────┘

START
  │
  ├─> Browse Products (Public)
  │   └─> Filter by category, price, search
  │
  ├─> Register/Login
  │   └─> Receive JWT token
  │
  ├─> Add Products to Cart
  │   ├─> Validate stock
  │   └─> Calculate total
  │
  ├─> Update Cart (Optional)
  │   └─> Change quantities
  │
  ├─> Place Order
  │   ├─> Validate cart
  │   ├─> Check stock
  │   ├─> Process payment (mock)
  │   ├─> Create order
  │   ├─> Reduce stock
  │   ├─> Clear cart
  │   └─> Send email
  │
  ├─> Track Order Status
  │   └─> View order history
  │
  └─> Receive Product
      └─> Order marked DELIVERED

ADMIN PATH:
  │
  ├─> Manage Products
  │   └─> Create/Update/Delete
  │
  ├─> Manage Orders
  │   └─> Update order status
  │
  └─> View Analytics
      ├─> Revenue reports
      ├─> Top products
      ├─> Low stock alerts
      └─> Active users
```

## 🔄 How It Works

### Complete Shopping Flow
```
1. Browse Products (Public)
   GET /api/products
   
2. Register/Login
   POST /auth/login → Returns JWT token
   
3. Add to Cart
   POST /api/cart/items {productId: 1, quantity: 2}
   
4. Place Order
   POST /api/orders → Creates order, reduces stock, clears cart
   
5. Track Order
   GET /api/orders/my-orders
```

### Order Processing Logic
```
Cart → Validate Stock → Create Order → Update Inventory → Clear Cart → Send Email
         └─ If any step fails, entire transaction rolls back (@Transactional)
```

## ⚙️ Configuration

### Database
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shop_db
    username: shopuser
    password: your_password
```

### JWT Settings
```yaml
jwt:
  secret: YOUR_SECRET_KEY_CHANGE_IN_PRODUCTION
  expiration-ms: 3600000  # 1 hour
```

### Email (Optional)
```yaml
spring:
  mail:
    host: smtp.gmail.com
    username: your-email@gmail.com
    password: your-app-password
```

## 🧪 Testing

```bash
# Run all tests (38 tests)
mvn test

# Run specific test
mvn test -Dtest=AuthServiceTest

# Skip tests during build
mvn clean install -DskipTests
```

Tests use **H2 in-memory database** - no MySQL required!

## 📦 Deployment

### Local
```bash
mvn clean package
java -jar target/shop-backend-0.0.1-SNAPSHOT.jar
```

### Docker
```bash
docker build -t shop-backend .
docker run -p 8080:8080 shop-backend
```

### Docker Compose
```bash
docker-compose up -d
```

## 📁 Project Structure

```
src/
├── main/java/com/example/shop/
│   ├── controller/      # REST endpoints
│   ├── service/         # Business logic
│   ├── repository/      # Data access
│   ├── model/           # JPA entities
│   ├── dto/             # Request/Response objects
│   ├── config/          # Security, JWT, Swagger
│   └── util/            # Helper classes
└── resources/
    └── application.yml  # Configuration
```

## 🔒 Security Features

| Feature | Implementation |
|---------|---------------|
| Password | BCrypt hashing (strength 10) |
| Auth | JWT tokens (HS256, 1hr expiry) |
| API Protection | Rate limiting (5-100 req/min) |
| CORS | Configured for localhost dev |
| Validation | Jakarta Bean Validation |
| SQL Injection | JPA parameterized queries |

## 📊 API Rate Limits

| Endpoint | Limit |
|----------|-------|
| `/auth/login` | 5 requests/minute |
| `/api/orders` | 10 requests/minute |
| All other APIs | 100 requests/minute |

## 🎯 Key Features Explained

<details>
<summary><b>Why Price Snapshots?</b></summary>

Order items store product name and price at the time of purchase. Even if the product changes later, order history remains accurate.

```java
// Order placed today
OrderItem { productName: "Laptop", priceEach: $1000 }

// Product updated tomorrow
Product { name: "Laptop Pro 2.0", price: $1200 }

// Order still shows original price
OrderItem { productName: "Laptop", priceEach: $1000 } ✓
```
</details>

<details>
<summary><b>Stock Management</b></summary>

- Adding to cart doesn't reserve stock
- Stock is reduced when order is placed
- Cancelled orders restore stock automatically
- Low stock alerts when inventory ≤ 10
</details>

<details>
<summary><b>Transaction Safety</b></summary>

All critical operations use `@Transactional`:
- Order placement (8 steps, all or nothing)
- Stock updates (prevents overselling)
- Cart operations (consistency guaranteed)
</details>
