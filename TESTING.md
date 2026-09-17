# TESTING STRATEGY
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | Version: 1.0 | Updated: 2026-09-17

---

## Overview

This project implements **7 levels of automated testing** as required by the DevOps Laboratory specification. Every feature must pass all applicable test levels before being marked COMPLETE.

---

## Test Level Definitions

### Level 1 — Unit Tests
**Tool**: JUnit 5 + Mockito  
**Location**: `src/test/java/com/printqueue/service/`, `src/test/java/com/printqueue/security/`  
**Naming**: `*Test.java`  
**Scope**: Individual classes in isolation. All dependencies mocked.  
**Run**: `mvn test -Dtest="*Test"`

Covers:
- `UserServiceTest` — Registration, login, business rules (18 tests)
- `JwtTokenProviderTest` — Token generation, parsing, validation (9 tests)

### Level 2 — Repository / Database Tests
**Tool**: Spring Data JPA Test + H2 in-memory (MySQL MODE)  
**Location**: `src/test/java/com/printqueue/repository/`  
**Naming**: `*RepositoryTest.java`  
**Scope**: Entity persistence, queries, constraints, relationships.  
**Run**: `mvn test -Dtest="*RepositoryTest"`

Covers:
- `UserRepositoryTest` — Save, find, unique constraint, status/role queries (15 tests)

### Level 3 — API Tests
**Tool**: Spring Boot Test + MockMvc  
**Location**: `src/test/java/com/printqueue/controller/`  
**Naming**: `*ControllerTest.java`  
**Scope**: HTTP endpoints — status codes, request/response JSON, validation, auth.  
**Run**: `mvn test -Dtest="*ControllerTest"`

Covers:
- `AuthControllerTest` — Register, login, logout, /me, role protection (18 tests)

### Level 4 — Integration Tests
**Tool**: Spring Boot Test (full context) + H2  
**Location**: `src/test/java/com/printqueue/`  
**Naming**: `*Test.java` (full context annotation)  
**Scope**: Controller → Service → Repository → Database (H2).  
**Run**: `mvn test -Dtest="PrintQueueApplicationTest"`

Covers:
- `PrintQueueApplicationTest` — Context loads, beans registered (3 tests)

### Level 5 — End-to-End Tests
**Tool**: Manual verification + curl scripts  
**Location**: `scripts/e2e/`  
**Scope**: Complete user workflows against running application.

Workflows:
1. Register → Login → Submit Job → View Queue → Cancel Job
2. Admin login → View all jobs → Manage printer → Pause queue

Commands (run against started app):
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@test.com","password":"Test@123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test@123"}'

# Health check
curl http://localhost:8080/actuator/health
```

### Level 6 — Docker Smoke Tests
**Tool**: Shell script + Docker Compose  
**Location**: `smoke-test.sh`  
**Scope**: Containers start, health endpoint responds.

```bash
#!/bin/bash
docker-compose up -d
sleep 30
curl -f http://localhost:8080/actuator/health | grep "UP"
echo "Smoke test PASSED"
```

### Level 7 — CI/CD Tests (Jenkins)
**Tool**: Jenkins Declarative Pipeline  
**Location**: `Jenkinsfile`  
**Scope**: Full automated pipeline — build, test, docker, deploy, verify.

---

## Test Commands Reference

```bash
# Compile (no tests)
mvn clean compile

# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run specific test method
mvn test -Dtest=UserServiceTest#testRegisterSuccess

# Run integration tests
mvn verify

# Run with verbose output
mvn test -Dsurefire.useFile=false

# Generate test report
mvn surefire-report:report
# Report: target/site/surefire-report.html
```

---

## Test Summary (Complete Regression Suite — Phases 1 to 14)

| Test Class | Level | Tests | Status |
|-----------|-------|-------|--------|
| `PrintQueueApplicationTest` | Level 5 — Full Context Integration | 3 | ✅ |
| `UserRepositoryTest` | Level 2 — Repository / DB | 15 | ✅ |
| `UserServiceTest` | Level 1 — Unit / Business Logic | 18 | ✅ |
| `JwtTokenProviderTest` | Level 1 — Security Unit | 9 | ✅ |
| `PrintJobServiceTest` | Level 1 — Business Logic | 5 | ✅ |
| `QueueServiceTest` | Level 1 — Queue Scheduling Logic | 4 | ✅ |
| `VirtualPrinterServiceTest` | Level 1 — Hardware Simulation | 2 | ✅ |
| `PrintJobHistoryServiceTest` | Level 1 — Audit Log Service | 1 | ✅ |
| `AdminServiceTest` | Level 1 — Admin Analytics Logic | 4 | ✅ |
| `DatabaseConsistencyTest` | Level 2 & 3 — Relational Integrity & Cascades | 3 | ✅ |
| `PrinterStateConsistencyTest` | Level 3 — FSM State Transitions | 4 | ✅ |
| `QueueConsistencyTest` | Level 3 — Queue Concurrency & Position | 1 | ✅ |
| `AuthControllerTest` | Level 4 — REST API / Security | 18 | ✅ |
| `PrintJobControllerTest` | Level 4 — REST API / Multipart | 2 | ✅ |
| `QueueControllerTest` | Level 4 — REST API / Live Queue | 2 | ✅ |
| `PrinterControllerTest` | Level 4 — REST API / Printer Control | 2 | ✅ |
| `AdminControllerTest` | Level 4 — REST API / Admin Authorization | 4 | ✅ |
| `FrontendPagesIntegrationTest` | Level 5 — Frontend Availability (13 Pages) | 14 | ✅ |
| `PrintQueueE2ETest` | Level 5 — End-to-End User Journeys | 9 | ✅ |
| **TOTAL AUTOMATED REGRESSION SUITE** | | **118** | ✅ **100% PASSED** |

> Complete QA details and test matrix documented in [QA_REPORT.md](QA_REPORT.md).

---

## Coverage Targets

| Layer | Target |
|-------|--------|
| Service layer | 80%+ line coverage |
| Controller layer | 70%+ line coverage |
| Repository layer | Tested via @DataJpaTest |
| Security layer | JWT + filter tested |

---

## Test Database

All automated tests use **H2 in-memory database** in MySQL compatibility mode:
```
jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
```

Integration tests requiring real MySQL are tagged `@Tag("integration")` and excluded from default test run. They run in Jenkins pipeline where MySQL is available via Docker.

---

## Regression Policy

1. New feature tests MUST pass before phase is declared complete
2. All existing tests MUST continue passing
3. If a test fails due to intentional behavior change:
   - Document the reason
   - Update the test to match new behavior
   - Add a regression test for the new behavior
4. **Empty tests or always-passing tests are FORBIDDEN**
5. Mocking must verify real behavior, not stub everything to true
