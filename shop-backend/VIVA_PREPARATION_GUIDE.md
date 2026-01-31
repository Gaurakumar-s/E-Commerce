# Viva Preparation Guide - Online Shopping Backend

## 📚 Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Request Flow](#request-flow)
3. [Layer-by-Layer Explanation](#layer-by-layer-explanation)
4. [Security & Authentication Flow](#security--authentication-flow)
5. [Database Design](#database-design)
6. [Key Features Explained](#key-features-explained)
7. [Common Viva Questions](#common-viva-questions)

---

## 🏗️ Architecture Overview

### **Layered Architecture (3-Tier)**

```
┌─────────────────────────────────────┐
│      CONTROLLER LAYER               │  ← REST API Endpoints (Thin Layer)
│  (AuthController, ProductController) │     - No business logic
│                                      │     - Only handles HTTP requests/responses
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       SERVICE LAYER                  │  ← Business Logic
│  (AuthService, ProductService, etc.) │     - All business rules
│                                      │     - Transaction management
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      REPOSITORY LAYER                │  ← Data Access
│  (UserRepository, ProductRepository) │     - Spring Data JPA
│                                      │     - Database operations
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         DATABASE                     │  ← MySQL/PostgreSQL
│      (shop_db)                       │
└─────────────────────────────────────┘
```

### **Package Structure**
```
com.example.shop/
├── config/          → Configuration (Security, Swagger, File Storage)
├── controller/      → REST Controllers (HTTP endpoints)
├── service/         → Business Logic Layer
├── repository/      → Data Access Layer (JPA Repositories)
├── model/           → JPA Entities (Database tables)
├── dto/             → Data Transfer Objects (Request/Response)
├── exception/       → Exception Handling
└── util/            → Utility Classes (JWT, Rate Limiter)
```

---

## 🔄 Request Flow

### **Example: User Places an Order**

```
1. Client Request
   POST /api/orders
   Headers: Authorization: Bearer <JWT_TOKEN>
   Body: { "paymentMethod": "credit_card" }

2. Rate Limiting Filter
   ├─ Checks if request exceeds rate limit
   └─ If exceeded → Returns 429 (Too Many Requests)

3. JWT Authentication Filter
   ├─ Extracts token from Authorization header
   ├─ Validates token using JwtUtil
   ├─ Loads user details from database
   └─ Sets authentication in SecurityContext

4. Spring Security
   ├─ Checks if endpoint requires authentication
   └─ Verifies user has required role

5. OrderController
   ├─ Receives request
   ├─ Validates @Valid PlaceOrderRequest
   └─ Calls OrderService.placeOrder()

6. OrderService (Business Logic)
   ├─ Validates cart exists and has items
   ├─ Checks product availability
   ├─ Calculates total amount
   ├─ Creates Order and OrderItems
   ├─ Updates product stock
   ├─ Clears cart
   ├─ Sends email notification
   └─ Returns OrderResponse

7. OrderController
   └─ Returns HTTP 201 Created with OrderResponse

8. Global Exception Handler (if error occurs)
   └─ Catches exceptions and returns standardized ErrorResponse
```

---

## 📦 Layer-by-Layer Explanation

### **1. CONTROLLER LAYER** (`controller/`)

**Purpose**: Handle HTTP requests and responses only. NO business logic.

**Key Annotations**:
- `@RestController` - Marks as REST controller, auto-converts to JSON
- `@RequestMapping("/api/products")` - Base URL mapping
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` - HTTP methods
- `@PathVariable` - URL path variables (`/products/{id}`)
- `@RequestParam` - Query parameters (`?categoryId=1`)
- `@RequestBody` - Request body (JSON)
- `@Valid` - Triggers Jakarta Validation
- `@PreAuthorize("hasRole('ADMIN')")` - Method-level security

**Example - ProductController**:
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    // Admin only - creates product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // Public - anyone can view products
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(...) {
        // Delegates to service layer
    }
}
```

**Key Points for Viva**:
- Controllers are THIN - they only delegate to services
- No business logic in controllers
- Use DTOs (Data Transfer Objects) to decouple API from entities
- `@Valid` triggers validation on DTOs before reaching service

---

### **2. SERVICE LAYER** (`service/`)

**Purpose**: Contains ALL business logic, transaction management, caching.

**Key Annotations**:
- `@Service` - Marks as Spring service bean
- `@Transactional` - Manages database transactions
- `@Cacheable` - Caches method results
- `@CacheEvict` - Removes from cache when data changes

**Example - ProductService**:
```java
@Service
public class ProductService {
    
    @Transactional  // All DB operations in one transaction
    @CacheEvict(value = {"products", "productById"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        // 1. Validate category exists
        Category category = categoryRepository.findById(...)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        // 2. Create product entity
        Product product = new Product();
        product.setName(request.getName());
        // ... set other fields
        
        // 3. Save to database
        Product saved = productRepository.save(product);
        
        // 4. Convert to DTO and return
        return toResponse(saved);
    }
    
    @Cacheable(value = "productById", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        // Cached - won't hit DB if already cached
    }
}
```

**Key Points for Viva**:
- All business rules are here (e.g., "check stock before order")
- `@Transactional` ensures data consistency (all or nothing)
- `@Cacheable` improves performance (stores results in memory)
- `@CacheEvict` ensures cache stays fresh when data changes
- Services can call other services (e.g., OrderService calls EmailService)

---

### **3. REPOSITORY LAYER** (`repository/`)

**Purpose**: Data access layer. Communicates with database.

**Key Features**:
- Extends `JpaRepository<Entity, ID>` - provides CRUD operations automatically
- Custom query methods using Spring Data JPA naming conventions
- `JpaSpecificationExecutor` for dynamic queries

**Example - ProductRepository**:
```java
public interface ProductRepository extends 
    JpaRepository<Product, Long>, 
    JpaSpecificationExecutor<Product> {
    
    // Spring automatically implements this based on method name
    Page<Product> findByActiveTrue(Pageable pageable);
    
    // Custom queries using Specifications (for filtering)
    // Used in ProductService.search()
}
```

**Key Points for Viva**:
- No implementation needed - Spring Data JPA generates it
- Method names define queries: `findByActiveTrue` → `SELECT * FROM products WHERE active = true`
- `Pageable` enables pagination automatically
- `JpaSpecificationExecutor` allows dynamic queries (used for product filtering)

---

### **4. MODEL LAYER** (`model/`)

**Purpose**: JPA Entities representing database tables.

**Key Annotations**:
- `@Entity` - Marks as JPA entity
- `@Table(name = "users")` - Database table name
- `@Id` - Primary key
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-increment
- `@Column` - Column mapping
- `@ManyToOne`, `@OneToMany` - Relationships
- `@PrePersist`, `@PreUpdate` - Lifecycle callbacks

**Example - User Entity**:
```java
@Entity
@Table(name = "users")
@Getter @Setter @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;  // Stored in separate table: user_roles
    
    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();  // Auto-set before save
    }
}
```

**Key Points for Viva**:
- Entities represent database tables
- `@PrePersist` runs before saving (sets timestamps)
- `@ManyToOne` = Foreign key relationship (Product → Category)
- `@OneToMany` = One-to-many relationship (Order → OrderItems)
- `@ElementCollection` = Collection stored in separate table (User → Roles)

---

### **5. DTO LAYER** (`dto/`)

**Purpose**: Data Transfer Objects - separate API contracts from entities.

**Why DTOs?**
- Hide internal entity structure from API
- Control what data is exposed
- Add validation annotations
- Prevent lazy loading issues

**Example**:
```java
// Request DTO - what client sends
public class ProductRequest {
    @NotBlank
    @Size(min = 2, max = 200)
    private String name;
    
    @NotNull
    @Min(0)
    private BigDecimal price;
}

// Response DTO - what client receives
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String categoryName;  // Flattened from entity
}
```

**Key Points for Viva**:
- DTOs decouple API from database structure
- Validation happens on DTOs, not entities
- Response DTOs can combine data from multiple entities

---

## 🔐 Security & Authentication Flow

### **JWT Authentication Flow**

```
1. USER REGISTRATION
   POST /auth/register
   ├─ AuthController receives RegisterRequest
   ├─ AuthService.registerCustomer():
   │   ├─ Checks if email exists
   │   ├─ Encodes password with BCrypt
   │   ├─ Creates User entity with Role.CUSTOMER
   │   ├─ Saves to database
   │   └─ Sends welcome email
   └─ Returns UserDto

2. USER LOGIN
   POST /auth/login
   ├─ AuthController receives LoginRequest
   ├─ AuthService.login():
   │   ├─ AuthenticationManager.authenticate()
   │   │   ├─ CustomUserDetailsService loads user from DB
   │   │   ├─ BCryptPasswordEncoder verifies password
   │   │   └─ Returns Authentication object
   │   ├─ JwtUtil.generateToken(email)
   │   │   ├─ Creates JWT with subject=email
   │   │   ├─ Sets expiration (1 hour)
   │   │   └─ Signs with secret key (HS256)
   │   └─ Returns AuthResponse with token
   └─ Client stores token

3. PROTECTED REQUEST
   GET /api/cart
   Headers: Authorization: Bearer <token>
   
   ├─ RateLimitingFilter
   │   └─ Checks rate limit (100 requests/min)
   
   ├─ JwtAuthenticationFilter
   │   ├─ Extracts token from header
   │   ├─ JwtUtil.extractUsername(token)
   │   ├─ JwtUtil.isTokenValid(token, username)
   │   ├─ CustomUserDetailsService.loadUserByUsername(email)
   │   ├─ Creates UsernamePasswordAuthenticationToken
   │   └─ Sets in SecurityContext
   
   ├─ Spring Security
   │   └─ Checks if user is authenticated
   
   └─ CartController
       └─ @AuthenticationPrincipal gives access to user
```

### **Security Configuration**

**SecurityConfig.java**:
```java
@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // JWT doesn't need CSRF
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // No sessions - stateless JWT authentication
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/api/products").permitAll()
                // Public endpoints
                .anyRequest().authenticated()
                // All others require authentication
            )
            .addFilterBefore(rateLimitingFilter, ...)
            .addFilterBefore(jwtAuthenticationFilter, ...);
    }
}
```

**Key Points for Viva**:
- **Stateless**: No server-side sessions (JWT is self-contained)
- **BCrypt**: One-way hashing for passwords (can't decrypt)
- **JWT Structure**: Header.Payload.Signature
- **Token Validation**: Checks signature, expiration, and username match
- **Role-Based Access**: `@PreAuthorize("hasRole('ADMIN')")` checks user roles

---

## 🗄️ Database Design

### **Entity Relationships**

```
User (1) ────< (M) Cart
User (1) ────< (M) Order
Order (1) ────< (M) OrderItem
Product (M) ────> (1) Category
Cart (1) ────< (M) CartItem
CartItem (M) ────> (1) Product
OrderItem (M) ────> (1) Product
```

### **Key Tables**

1. **users**
   - id (PK)
   - email (unique)
   - password_hash (BCrypt)
   - name, address fields
   - created_at, updated_at

2. **user_roles** (ElementCollection)
   - user_id (FK)
   - role (ADMIN/CUSTOMER)

3. **products**
   - id (PK)
   - name, description, price
   - stock_quantity
   - category_id (FK → categories)
   - image_url
   - active (boolean)
   - created_at, updated_at

4. **orders**
   - id (PK)
   - user_id (FK → users)
   - total_amount
   - status (CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
   - payment_status (PENDING, PAID, FAILED, REFUNDED)
   - payment_reference
   - created_at

5. **order_items**
   - id (PK)
   - order_id (FK → orders)
   - product_id (FK → products)
   - product_name_snapshot (preserves name at time of order)
   - quantity, price_each, subtotal

**Key Points for Viva**:
- **Foreign Keys**: Maintain referential integrity
- **Snapshots**: OrderItem stores product name/price at order time (product may change later)
- **Cascade**: Order deletion cascades to OrderItems
- **Lazy Loading**: `@ManyToOne(fetch = FetchType.LAZY)` - loads only when accessed

---

## ⚙️ Key Features Explained

### **1. Pagination & Sorting**

**How it works**:
```java
@GetMapping
public ResponseEntity<Page<ProductResponse>> search(Pageable pageable) {
    // Pageable contains: page number, size, sort direction
    // Example: ?page=0&size=20&sort=createdAt,desc
    
    Page<ProductResponse> page = productService.search(..., pageable);
    // Returns: content, totalElements, totalPages, etc.
}
```

**Spring Data JPA automatically**:
- Adds `LIMIT` and `OFFSET` to SQL
- Handles sorting with `ORDER BY`
- Returns `Page` object with metadata

---

### **2. Filtering (Dynamic Queries)**

**ProductSpecification.java**:
```java
public class ProductSpecification {
    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) ->
            categoryId == null ? null : 
            cb.equal(root.get("category").get("id"), categoryId);
    }
    
    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
            minPrice == null ? null :
            cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }
}
```

**Usage in ProductService**:
```java
Specification<Product> spec = Specification
    .where(ProductSpecification.hasCategory(categoryId))
    .and(ProductSpecification.priceGreaterThanOrEqual(minPrice))
    .and(ProductSpecification.nameOrDescriptionContains(search));

return productRepository.findAll(spec, pageable);
```

**Key Points**:
- Builds SQL query dynamically based on provided filters
- Null filters are ignored (optional filtering)
- Combines multiple conditions with `and()`

---

### **3. Caching**

**How it works**:
```java
@Cacheable(value = "productById", key = "#id")
public ProductResponse getById(Long id) {
    // First call: hits database, stores in cache
    // Subsequent calls: returns from cache (no DB hit)
}

@CacheEvict(value = {"products", "productById"}, allEntries = true)
public ProductResponse create(ProductRequest request) {
    // When product is created, cache is cleared
    // Next getById() will fetch fresh data
}
```

**Cache Strategy**:
- **Read-heavy data**: Cache product lists, categories
- **Cache invalidation**: Clear cache when data changes
- **Cache keys**: Use entity IDs for specific lookups

---

### **4. File Upload**

**Flow**:
```
1. Client sends multipart/form-data
   POST /api/products/{id}/image
   Content-Type: multipart/form-data
   Body: file=<image>

2. FileStorageService.storeFile()
   ├─ Generates unique filename (UUID)
   ├─ Saves to uploads/ directory
   └─ Returns URL: /uploads/{filename}

3. ProductService.updateImage()
   ├─ Updates product.imageUrl in database
   └─ Clears cache

4. FileStorageConfig
   └─ Maps /uploads/** to file system
      Serves files as static resources
```

**Key Points**:
- Files stored on disk (not in database)
- Database stores only the URL/path
- Static resource handler serves files

---

### **5. Email Integration**

**EmailService.java**:
```java
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    public void sendOrderConfirmationEmail(...) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Order Confirmation");
        message.setText("...");
        mailSender.send(message);
    }
}
```

**Configuration** (application.yml):
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

**Key Points**:
- Uses Spring Mail abstraction
- SMTP configuration externalized
- Email sending doesn't block (async in production)

---

### **6. Rate Limiting**

**RateLimiter.java**:
```java
public class RateLimiter {
    private final Map<String, RateLimitInfo> rateLimitMap;
    private final int maxRequests;
    private final long windowMs;
    
    public boolean isAllowed(String key) {
        // Checks if requests in current time window < maxRequests
        // Resets window after windowMs milliseconds
    }
}
```

**RateLimitingFilter.java**:
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final RateLimiter loginRateLimiter = new RateLimiter(5, 60000);
    // 5 requests per 60 seconds (1 minute)
    
    @Override
    protected void doFilterInternal(...) {
        if (path.equals("/auth/login")) {
            if (!loginRateLimiter.isAllowed(clientId)) {
                response.setStatus(429);  // Too Many Requests
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

**Key Points**:
- In-memory rate limiting (simple implementation)
- Different limits for different endpoints
- Client identified by IP or token prefix

---

### **7. Analytics (Complex Queries)**

**AnalyticsService.java**:
```java
public List<TopProductResponse> getTopProductsByRevenue(...) {
    // 1. Get all paid orders in date range
    List<Order> orders = orderRepository.findAll()
        .stream()
        .filter(order -> order.getPaymentStatus() == PaymentStatus.PAID)
        .collect(Collectors.toList());
    
    // 2. Flatten order items
    // 3. Group by product ID
    // 4. Sum quantities and revenue
    // 5. Sort by revenue descending
    // 6. Limit to top N
}
```

**Key Points**:
- Uses Java Streams for data processing
- Groups and aggregates data in memory
- Could be optimized with native SQL queries for large datasets

---

### **8. Global Exception Handling**

**GlobalExceptionHandler.java**:
```java
@RestControllerAdvice  // Applies to all controllers
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(...) {
        // Catches all IllegalArgumentException from any controller
        // Returns standardized error response
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(...) {
        // Catches validation errors from @Valid
        // Returns field-level error messages
    }
}
```

**Key Points**:
- Centralized error handling
- Consistent error response format
- Maps exceptions to appropriate HTTP status codes

---

## ❓ Common Viva Questions

### **Q1: Explain the request flow when a user places an order.**

**Answer**:
1. Client sends POST `/api/orders` with JWT token
2. RateLimitingFilter checks rate limit
3. JwtAuthenticationFilter validates token and loads user
4. Spring Security verifies authentication
5. OrderController receives request and validates DTO
6. OrderController calls OrderService.placeOrder()
7. OrderService:
   - Validates cart exists and has items
   - Checks product availability and stock
   - Creates Order and OrderItems
   - Updates product stock quantities
   - Clears cart
   - Sends confirmation email
8. Returns OrderResponse to client

---

### **Q2: How does JWT authentication work?**

**Answer**:
- **Registration**: User registers, password is hashed with BCrypt and stored
- **Login**: User provides email/password, Spring Security authenticates, JwtUtil generates token
- **Token Structure**: Header (algorithm), Payload (email, expiration), Signature (HMAC)
- **Protected Requests**: Client sends token in Authorization header
- **Validation**: JwtAuthenticationFilter extracts token, validates signature and expiration, loads user, sets in SecurityContext
- **Stateless**: No server-side session, token is self-contained

---

### **Q3: Why do we use DTOs instead of returning entities directly?**

**Answer**:
1. **Security**: Hide internal structure (e.g., password hash)
2. **Performance**: Prevent lazy loading issues (N+1 queries)
3. **API Contract**: Control what data is exposed
4. **Validation**: Add validation annotations without affecting entities
5. **Flexibility**: Combine data from multiple entities in response

---

### **Q4: Explain the caching strategy.**

**Answer**:
- **What's cached**: Product lists, individual products, categories
- **Cache keys**: Entity IDs for specific lookups
- **Cache eviction**: When products/categories are created/updated/deleted, cache is cleared
- **Read-heavy data**: Products and categories are cached because they're frequently read
- **Write operations**: Always clear cache to ensure data consistency

---

### **Q5: How does pagination work?**

**Answer**:
- Spring Data JPA's `Pageable` interface
- Client sends: `?page=0&size=20&sort=createdAt,desc`
- Repository automatically adds `LIMIT` and `OFFSET` to SQL
- Returns `Page<T>` object containing:
  - `content`: List of items
  - `totalElements`: Total count
  - `totalPages`: Number of pages
  - `number`: Current page number

---

### **Q6: Explain the database relationships.**

**Answer**:
- **User → Cart**: One-to-One (each user has one cart)
- **User → Orders**: One-to-Many (user can have multiple orders)
- **Order → OrderItems**: One-to-Many (order contains multiple items)
- **Product → Category**: Many-to-One (products belong to category)
- **Cart → CartItems**: One-to-Many
- **CartItem → Product**: Many-to-One
- **OrderItem → Product**: Many-to-One (with snapshot of product name/price)

---

### **Q7: What is @Transactional and why is it important?**

**Answer**:
- **Purpose**: Ensures all database operations in a method succeed or all fail (ACID)
- **Example**: In `placeOrder()`, if stock update fails, order creation is rolled back
- **Read-only**: `@Transactional(readOnly = true)` optimizes for read operations
- **Isolation**: Prevents dirty reads, ensures data consistency

---

### **Q8: How does rate limiting work?**

**Answer**:
- **Implementation**: In-memory map tracking requests per client
- **Time Window**: Sliding window (e.g., 1 minute)
- **Different Limits**: 
  - Login: 5/min (prevent brute force)
  - Orders: 10/min (prevent abuse)
  - General: 100/min
- **Client Identification**: IP address or token prefix
- **Response**: Returns HTTP 429 (Too Many Requests) when exceeded

---

### **Q9: Explain the filtering mechanism.**

**Answer**:
- Uses **JPA Specifications** for dynamic queries
- `ProductSpecification` class contains static methods that build query predicates
- Conditions are combined with `and()`
- Null filters are ignored (optional filtering)
- Example: Filter by category AND price range AND search text

---

### **Q10: What happens when a product is updated?**

**Answer**:
1. Admin sends PUT `/api/products/{id}` with updated data
2. ProductService.update() is called
3. `@CacheEvict` clears product cache
4. Product entity is updated in database
5. Cache is cleared so next read fetches fresh data
6. Returns updated ProductResponse

---

## 🎯 Key Concepts to Remember

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Stateless Authentication**: JWT tokens, no server sessions
3. **Transaction Management**: `@Transactional` ensures data consistency
4. **Caching Strategy**: Cache reads, invalidate on writes
5. **DTO Pattern**: Decouple API from database structure
6. **Exception Handling**: Centralized with `@RestControllerAdvice`
7. **Validation**: Jakarta Validation on DTOs
8. **Security**: Role-based access control with `@PreAuthorize`
9. **Pagination**: Spring Data JPA handles automatically
10. **Dynamic Queries**: JPA Specifications for flexible filtering

---

## 📝 Quick Reference

### **Annotations Cheat Sheet**

| Annotation | Purpose | Where Used |
|------------|---------|------------|
| `@Entity` | JPA entity | Model classes |
| `@Service` | Business logic | Service classes |
| `@Repository` | Data access | Repository interfaces |
| `@RestController` | REST controller | Controller classes |
| `@Transactional` | Transaction management | Service methods |
| `@Cacheable` | Cache results | Service methods (reads) |
| `@CacheEvict` | Clear cache | Service methods (writes) |
| `@PreAuthorize` | Method security | Controller methods |
| `@Valid` | Trigger validation | Controller parameters |
| `@RestControllerAdvice` | Global exception handling | Exception handler |

---

Good luck with your viva! 🚀
