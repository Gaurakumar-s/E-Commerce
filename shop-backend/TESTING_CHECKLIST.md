# Testing Checklist - Before Viva

## ✅ Pre-Test Verification

- [ ] H2 database dependency added to `pom.xml`
- [ ] `application-test.yml` exists in `src/test/resources`
- [ ] All test files are in `src/test/java/com/example/shop/`
- [ ] Test profile is configured correctly

## 🧪 Test Execution

### **Step 1: Run All Tests**
```bash
cd shop-backend
mvn clean test
```

**Expected**: All tests pass (34+ tests)

### **Step 2: Verify Test Results**
Check console output for:
```
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### **Step 3: Run Specific Test Categories**

```bash
# Service tests only
mvn test -Dtest=*ServiceTest

# Controller tests only
mvn test -Dtest=*ControllerTest

# Integration tests
mvn test -Dtest=*IntegrationTest
```

## 📋 Test Files Checklist

- [x] `ShopBackendApplicationTests.java` - Context loads
- [x] `AuthServiceTest.java` - 3 test methods
- [x] `ProductServiceTest.java` - 7 test methods
- [x] `OrderServiceTest.java` - 4 test methods
- [x] `CartServiceTest.java` - 5 test methods
- [x] `AuthControllerTest.java` - 3 test methods
- [x] `ProductControllerTest.java` - 4 test methods
- [x] `UserRepositoryTest.java` - 4 test methods
- [x] `JwtUtilTest.java` - 4 test methods
- [x] `OrderIntegrationTest.java` - 1 test method
- [x] `GlobalExceptionHandlerTest.java` - 2 test methods
- [x] `SecurityConfigTest.java` - 1 test method

## 🎯 What Each Test Verifies

### **Service Tests**
- Business logic correctness
- Error handling
- Validation rules
- Transaction behavior

### **Controller Tests**
- HTTP endpoint responses
- Status codes
- JSON structure
- Security restrictions

### **Repository Tests**
- Database queries
- Entity persistence
- Query methods

### **Integration Tests**
- End-to-end flow
- Database interactions
- Service coordination

## 🐛 Common Issues & Fixes

### **Issue: Tests fail with "Bean not found"**
**Fix**: Ensure `@SpringBootTest` or `@WebMvcTest` is used correctly

### **Issue: Security tests fail**
**Fix**: Add `@WithMockUser` or use `.with(csrf())` for POST requests

### **Issue: Database connection error**
**Fix**: Verify H2 dependency and `application-test.yml` configuration

### **Issue: Mockito errors**
**Fix**: Ensure `@ExtendWith(MockitoExtension.class)` is present

## 📊 Test Coverage Summary

| Component | Coverage |
|-----------|----------|
| Services | High |
| Controllers | Medium-High |
| Repositories | Medium |
| Utilities | High |
| Integration | Basic |

## 🎤 Viva Preparation

**Be ready to explain**:

1. **"Show me a test case"**
   - Open any test file
   - Explain the AAA pattern
   - Show mocking usage

2. **"How do you test security?"**
   - `@WithMockUser` for authenticated endpoints
   - `.with(csrf())` for state-changing operations
   - Mock security context

3. **"What's the difference between unit and integration tests?"**
   - Unit: Isolated, mocked dependencies
   - Integration: Real database, full Spring context

4. **"How do you test exceptions?"**
   - `assertThrows()` for expected exceptions
   - Verify exception messages
   - Test exception handlers

5. **"Show me how you test the order placement"**
   - `OrderServiceTest` - unit test with mocks
   - `OrderIntegrationTest` - full flow with real DB

## ✅ Final Verification

Before viva, ensure:
- [ ] All tests compile without errors
- [ ] All tests pass when run
- [ ] You can explain any test file
- [ ] You understand mocking vs integration testing
- [ ] You know how to run tests from command line

---

**Run `mvn test` now to verify everything works!** 🚀
