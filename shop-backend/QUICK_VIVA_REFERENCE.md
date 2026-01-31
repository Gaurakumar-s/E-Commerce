# Quick Viva Reference - One Page Summary

## 🏗️ Architecture (3 Layers)

**Controller → Service → Repository → Database**

- **Controller**: HTTP handling only, delegates to service
- **Service**: Business logic, transactions, caching
- **Repository**: Data access (Spring Data JPA)

---

## 🔐 Authentication Flow

1. **Register**: Email/password → BCrypt hash → Save user → Send email
2. **Login**: Email/password → Authenticate → Generate JWT → Return token
3. **Protected Request**: Extract token → Validate → Load user → Set in SecurityContext

**JWT Structure**: Header.Payload.Signature (HS256 algorithm)

---

## 📊 Request Flow Example (Place Order)

```
Client → RateLimitingFilter → JwtAuthenticationFilter → Security → 
Controller → Service → Repository → Database
```

**OrderService.placeOrder()**:
1. Validate cart exists
2. Check product stock
3. Create Order + OrderItems
4. Update product stock
5. Clear cart
6. Send email
7. Return response

---

## 🗄️ Key Entities & Relationships

- **User** (1) → (M) **Order**
- **Order** (1) → (M) **OrderItem**
- **Product** (M) → (1) **Category**
- **User** (1) → (1) **Cart**
- **Cart** (1) → (M) **CartItem**

**Why OrderItem has product snapshot?** Product name/price may change, but order should preserve original values.

---

## ⚡ Key Features

### **Pagination**
- `Pageable` parameter → Spring adds LIMIT/OFFSET automatically
- Returns `Page<T>` with content + metadata

### **Filtering**
- JPA Specifications build dynamic queries
- Combine conditions with `and()`
- Null filters ignored

### **Caching**
- `@Cacheable`: Cache read operations
- `@CacheEvict`: Clear cache on write
- Cache keys: entity IDs

### **Rate Limiting**
- In-memory map tracks requests per client
- Different limits: Login (5/min), Orders (10/min), General (100/min)
- Returns 429 when exceeded

### **File Upload**
- Files stored on disk (`uploads/` directory)
- Database stores URL only
- Static resource handler serves files

### **Email**
- Spring Mail with SMTP
- Sent on: Registration, Order placement
- Configuration in `application.yml`

---

## 🔑 Important Annotations

| Annotation | Purpose |
|------------|---------|
| `@Transactional` | Ensures all DB operations succeed or fail together |
| `@Cacheable` | Cache method results |
| `@CacheEvict` | Clear cache when data changes |
| `@PreAuthorize` | Method-level security (role check) |
| `@Valid` | Trigger Jakarta Validation |
| `@RestControllerAdvice` | Global exception handler |

---

## ❓ Top 5 Viva Questions

### **1. Why DTOs?**
- Hide internal structure
- Prevent lazy loading issues
- Control API contract
- Add validation

### **2. How does JWT work?**
- Stateless authentication
- Token contains user info (email)
- Validated on every request
- No server-side session

### **3. What is @Transactional?**
- Ensures ACID properties
- All operations succeed or all rollback
- Prevents partial updates

### **4. Explain caching strategy**
- Cache frequently read data (products, categories)
- Clear cache on writes
- Improves performance

### **5. How does pagination work?**
- `Pageable` interface
- Spring Data JPA adds LIMIT/OFFSET
- Returns page with metadata

---

## 🎯 Remember These Points

1. **No business logic in controllers** - Only in services
2. **Stateless JWT** - No sessions stored on server
3. **DTOs decouple API from database**
4. **@Transactional ensures data consistency**
5. **Cache reads, invalidate on writes**
6. **Global exception handler** - Centralized error handling
7. **Role-based access** - `@PreAuthorize("hasRole('ADMIN')")`
8. **Dynamic queries** - JPA Specifications for filtering

---

## 📁 File Locations Quick Reference

- **Security**: `config/SecurityConfig.java`, `config/JwtAuthenticationFilter.java`
- **Business Logic**: `service/*.java`
- **API Endpoints**: `controller/*.java`
- **Database Access**: `repository/*.java`
- **Entities**: `model/*.java`
- **Request/Response**: `dto/*.java`
- **Exception Handling**: `exception/GlobalExceptionHandler.java`
- **Utilities**: `util/JwtUtil.java`, `util/RateLimiter.java`

---

**Good luck! 🚀**
