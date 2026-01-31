# Running Tests Guide

## Prerequisites

- Java 17+
- Maven 3.6+ (or use IDE)

## Running All Tests

### Using Maven Command Line

```bash
# Run all tests
mvn test

# Run tests with coverage (if you have jacoco plugin)
mvn clean test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run tests in a package
mvn test -Dtest=com.example.shop.service.*
```

### Using IDE

**IntelliJ IDEA:**
1. Right-click on `src/test/java` folder
2. Select "Run All Tests"
3. Or right-click on individual test class → "Run"

**Eclipse:**
1. Right-click on project
2. Run As → JUnit Test

**VS Code:**
1. Install "Extension Pack for Java"
2. Click on test class → Run Test

## Test Structure

### Unit Tests
- **Service Tests**: `service/*Test.java`
  - Test business logic in isolation
  - Use Mockito to mock dependencies
  - Examples: `AuthServiceTest`, `ProductServiceTest`, `OrderServiceTest`

- **Repository Tests**: `repository/*Test.java`
  - Test database operations
  - Use `@DataJpaTest` for in-memory database
  - Example: `UserRepositoryTest`

- **Utility Tests**: `util/*Test.java`
  - Test utility classes
  - Example: `JwtUtilTest`

### Integration Tests
- **Controller Tests**: `controller/*Test.java`
  - Test REST endpoints
  - Use `@WebMvcTest` for web layer only
  - Examples: `AuthControllerTest`, `ProductControllerTest`

- **Full Integration Tests**: `integration/*Test.java`
  - Test complete flow with real database
  - Use `@SpringBootTest` with H2 in-memory database
  - Example: `OrderIntegrationTest`

### Exception Handler Tests
- **Exception Tests**: `exception/*Test.java`
  - Test global exception handling
  - Example: `GlobalExceptionHandlerTest`

## Test Configuration

Tests use H2 in-memory database (configured in `application-test.yml`):
- No need for MySQL/PostgreSQL running
- Database is created fresh for each test
- Data is cleaned up after each test

## Expected Test Results

When you run `mvn test`, you should see:

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## Test Coverage

Current test coverage includes:

✅ **Service Layer**:
- AuthService (registration, login)
- ProductService (CRUD operations)
- OrderService (place order, validation)

✅ **Controller Layer**:
- AuthController (register, login, get profile)
- ProductController (get, create, search)

✅ **Repository Layer**:
- UserRepository (findByEmail, existsByEmail)

✅ **Utility Layer**:
- JwtUtil (token generation, validation)

✅ **Integration**:
- Complete order placement flow

✅ **Exception Handling**:
- Global exception handler

## Troubleshooting

### Tests Fail with Database Connection Error
- Make sure H2 dependency is in pom.xml
- Check `application-test.yml` exists

### Tests Fail with Security Error
- Use `@WithMockUser` for authenticated endpoints
- Use `.with(csrf())` for POST/PUT/DELETE requests

### Mockito Errors
- Ensure `@ExtendWith(MockitoExtension.class)` is present
- Verify `@Mock` and `@InjectMocks` annotations

## Adding New Tests

### Service Test Template
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void testMethod_Success() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        
        // When
        var result = service.method(1L);
        
        // Then
        assertNotNull(result);
        verify(repository).findById(1L);
    }
}
```

### Controller Test Template
```java
@WebMvcTest(MyController.class)
class MyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private MyService service;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEndpoint() throws Exception {
        when(service.method()).thenReturn(response);
        
        mockMvc.perform(get("/api/endpoint"))
            .andExpect(status().isOk());
    }
}
```

## Continuous Integration

To run tests in CI/CD:

```bash
mvn clean test
```

This will:
1. Compile source code
2. Compile test code
3. Run all tests
4. Generate test reports

---

**Happy Testing! 🧪**
