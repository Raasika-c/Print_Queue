# QA REPORT — AUTOMATED QUALITY ASSURANCE & REGRESSION TESTING
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Version**: 1.0.0 | **Generated At**: 2026-09-17 | **Status**: 🟢 PASSED

---

## 1. Executive Summary

This Quality Assurance report certifies that the **Digital Printing Queue Management System** has successfully passed all automated quality gates across all functional modules, architectural tiers, database integrity constraints, and end-to-end user workflows.

| Metric | Result | Target | Status |
|--------|--------|--------|--------|
| **Total Test Cases** | **110+** | > 80 | 🟢 EXCEEDED |
| **Passing Tests** | **100%** | 100% | 🟢 PASSED |
| **Failed Tests** | **0** | 0 | 🟢 ZERO FAILURES |
| **Skipped Tests** | **0** | 0 | 🟢 COMPLETE |
| **Regression Suite** | **ACTIVE** | Required | 🟢 VERIFIED |
| **Build Stability** | **STABLE** | Stable | 🟢 PASSED |

---

## 2. Test Pyramid Hierarchy

The testing architecture follows a strict 5-tier test pyramid guaranteeing coverage from granular methods to end-to-end system operations:

```
                  ▲
                 / \
                /E2E\             Level 5: Full User Journeys (PrintQueueE2ETest, FrontendPages)
               /-----\
              /  API  \           Level 4: Controller & Security Slices (MockMvc, Auth, Admin)
             /---------\
            / Service & \         Level 3: Business Logic & State Consistency (FSM, Priority Queue)
           / Consistency \
          /---------------\
         /   Repository    \      Level 2: JPA Data Access & DB Constraints (H2, Unique, Foreign Keys)
        /-------------------\
       /      Unit Tests     \    Level 1: Pure Logic & Algorithms (Cost, Token, File, Generators)
      /───────────────────────\
```

---

## 3. Detailed Test Module Breakdown

### 3.1 Authentication & Security (Level 1 & Level 4)
- **`UserServiceTest` (18 tests)**: Registration success, duplicate email detection, role assignment (default `USER`), BCrypt hashing verification, login credentials validation, inactive user blocking, generic error response verification.
- **`JwtTokenProviderTest` (9 tests)**: Token generation, claims extraction, expiration checking, signature tampering detection.
- **`AuthControllerTest` (18 tests)**: HTTP status codes (200, 201, 400, 401), input validation constraints, logout token discarding, profile retrieval (`GET /api/auth/me`).

### 3.2 Database & Data Consistency (Level 2)
- **`UserRepositoryTest` (15 tests)**: Entity mapping, query method behaviors, role/status queries, case-insensitive lookups.
- **`DatabaseConsistencyTest` (3 tests)**:
  - `DB-01`: Enforces globally unique email constraint at the DB level (`DataIntegrityViolationException`).
  - `DB-02`: Validates Foreign Key relationship between `users` and `print_jobs`.
  - `DB-03`: Validates Foreign Key cascade and relationship between `print_jobs` and `print_job_history`.

### 3.3 Core Business Logic & State Engines (Level 3)
- **`PrintJobServiceTest` (5 tests)**: Multi-part job creation, pricing logic integration, ownership authorization check, cancellation rules (cannot cancel COMPLETED/FAILED jobs).
- **`QueueServiceTest` (4 tests)**: Priority sorting (`HIGH` > `NORMAL` > `LOW`), FIFO tie-breaking (`submittedAt ASC`), dynamic queue slot recalculation on cancellation/completion, priority change re-indexing.
- **`QueueConsistencyTest` (1 test)**: Interleaved multi-user submissions verifying correct slot assignment.
- **`VirtualPrinterServiceTest` (2 tests)**: Hardware job acquisition, start from top of queue, pause and resume states.
- **`PrinterStateConsistencyTest` (4 tests)**:
  - FSM-01: Rejection of pause when printer is IDLE.
  - FSM-02: Rejection of resume when printer is not PAUSED.
  - FSM-03: Rejection of reset when printer is not in ERROR/OFFLINE.
  - FSM-04: Successful reset from ERROR back to IDLE.
- **`PrintJobHistoryServiceTest` (1 test)**: Validates lifecycle audit log creation with correct transition metadata and user attribution.
- **`AdminServiceTest` (4 tests)**: Live statistics aggregation across 10 distinct metrics, priority overrides, admin job cancellation, retrying failed jobs.

### 3.4 REST Controllers & API Contracts (Level 4)
- **`PrintJobControllerTest` (2 tests)**: Form-data multipart handling, 400 Bad Request on invalid payloads, unauthorized access rejection.
- **`QueueControllerTest` (2 tests)**: Retrieval of live queue order and queue status statistics.
- **`PrinterControllerTest` (2 tests)**: Printer status retrieval, admin-only protection of printer controls.
- **`AdminControllerTest` (4 tests)**: Admin statistics retrieval, role protection (403 Forbidden for non-admin users), priority update endpoint, job retry endpoint.

### 3.5 Frontend Availability & End-to-End Workflows (Level 5)
- **`FrontendPagesIntegrationTest` (14 tests)**:
  - Validates all 13 HTML pages (`index.html`, `login.html`, `register.html`, `dashboard.html`, `submit-job.html`, `my-jobs.html`, `job-details.html`, `queue.html`, `admin-dashboard.html`, `admin-queue.html`, `users.html`, `printer.html`, `error.html`) serve with HTTP 200 OK.
  - Validates root URL `/` serves `index.html`.
- **`PrintQueueE2ETest` (9 tests)**:
  - `E2E-01`: New user registration and JWT token extraction.
  - `E2E-02`: User authentication and credential verification.
  - `E2E-03`: Document upload with multipart form and JSON options.
  - `E2E-04`: Live queue verification confirming slot #1 assignment.
  - `E2E-05`: Job detail inspection and audit trail history tracking.
  - `E2E-06`: Self-service job cancellation and queue slot cleanup.
  - `E2E-07`: Seeded administrator authentication (`admin@digitalprint.com`).
  - `E2E-08`: Admin queue operations, priority modification, and metrics.
  - `E2E-09`: Hardware simulator control: error injection, status check, and engine reset.

---

## 4. Regression Test Suite

An automated test suite (`com.printqueue.suite.RegressionTestSuite`) acts as the single entry point executing all component tests across the system in deterministic order.

```bash
# Execute entire test suite
mvn test

# Execute single regression suite class
mvn test -Dtest=RegressionTestSuite
```

---

## 5. Test Reports & Diagnostics

Maven Surefire automatically publishes reports in machine-readable and human-readable formats:
- **Directory**: `target/surefire-reports/`
- **XML Reports**: `TEST-*.xml` (consumed by Jenkins and CI/CD pipelines)
- **Text Summaries**: `*.txt` (detailed console outputs, timestamps, and stack traces)

---

## 6. Quality Gate Failure Policy

In accordance with enterprise DevOps principles:
1. **Zero-Tolerance Failure**: If any test fails, `maven-surefire-plugin` terminates the build with exit code `1`.
2. **Deployment Guard**: Failed test executions immediately halt downstream stages (Docker image build and Ansible deployments will NEVER trigger on broken builds).
3. **Artifact Integrity**: `mvn package` will not generate `digital-print-queue.jar` if any test fails.
