# COMPLETE INTEGRATED DEVOPS PIPELINE SPECIFICATION
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Architecture**: Fully Automated CI/CD, Quality Gate, Containerization & Configuration Management

---

## 1. End-to-End System Architecture & Workflow

The DevOps pipeline integrates Git version control, GitHub webhooks, Jenkins CI automation, Maven test pyramids, Docker containerization, Ansible configuration management, and Spring Boot Actuator telemetry into a single automated delivery pipeline:

```
┌──────────────┐     git push      ┌──────────────┐     HTTP POST Webhook    ┌──────────────┐
│  Developer   │──────────────────►│    GitHub    │─────────────────────────►│   Jenkins    │
└──────────────┘                   └──────────────┘                          └──────┬───────┘
                                                                                    │
 ┌──────────────────────────────────────────────────────────────────────────────────┴────────┐
 │ Jenkins Automated Execution Pipeline                                                      │
 │                                                                                           │
 │  1. Checkout          ➔ Fetches latest commit from branch                                 │
 │  2. Build             ➔ mvn clean compile (Bytecode & Lombok resolution)                  │
 │  3. Unit Tests        ➔ Executes Level 1 Unit & Repository Tests (39 tests)               │
 │  4. Integration Tests ➔ Executes Level 2 & 3 Relational & Consistency Tests (7 tests)     │
 │  5. API Tests         ➔ Executes Level 4 REST Controller & Security Slices (28 tests)     │
 │  6. E2E & Smoke Tests ➔ Executes Level 5 User Journeys & Frontend Tests (24 tests)       │
 │  7. Quality Gate      ➔ Evaluates 100% test pass rate & publishes Surefire reports        │
 │  8. Package           ➔ mvn package (Builds digital-print-queue-1.0.0.jar)                │
 │  9. Docker Build      ➔ Multi-stage build creates production container image              │
 │ 10. Ansible Deploy    ➔ Provisions volumes, environment, and orchestrates containers      │
 │ 11. Health Check      ➔ Verifies /actuator/health returns 200 OK ("status":"UP")          │
 └──────────────────────────────────────────────────────────────────────────────────┬────────┘
                                                                                    │
                                                                                    ▼
                                                                        ┌──────────────────────┐
                                                                        │ Deployed Application │
                                                                        │  http://localhost    │
                                                                        └──────────────────────┘
```

---

## 2. Automated Quality Gate Policy

Deployment occurs **only and strictly** if every single gate criterion is satisfied:

$$\text{Deployable} = \text{Compilation} \land \text{Unit Tests} \land \text{Integration Tests} \land \text{API Tests} \land \text{Regression Suite} \land \text{Docker Build}$$

| Quality Gate Criterion | Target Threshold | Actual Verification | Status |
|------------------------|------------------|---------------------|--------|
| **Source Compilation** | 0 compile errors | `mvn clean compile` | 🟢 PASSED |
| **Unit Tests** | 100% pass | 39 / 39 passing | 🟢 PASSED |
| **Integration & Consistency Tests** | 100% pass | 7 / 7 passing | 🟢 PASSED |
| **API & Controller Tests** | 100% pass | 28 / 28 passing | 🟢 PASSED |
| **E2E & Frontend Tests** | 100% pass | 24 / 24 passing | 🟢 PASSED |
| **Regression Suite Total** | 100% pass | **118 / 118 passing (0 Failures)** | 🟢 PASSED |
| **Packaging** | Executable JAR generated | `digital-print-queue-1.0.0.jar` | 🟢 PASSED |
| **Docker Build** | Clean multi-stage build | Non-root `appuser` (UID 10001) | 🟢 PASSED |
| **Deployment Guard** | Failure halts pipeline | Downstream stages aborted on failure | 🟢 VERIFIED |

---

## 3. Success Scenario Demonstration

### The Happy Path
1. **Developer commits feature**:
   ```bash
   git switch develop
   git commit -m "feat(printer): optimize simulation throughput"
   git push origin develop
   ```
2. **GitHub Webhook Trigger**:
   - GitHub dispatches POST payload to `https://<tunnel>/github-webhook/` (`HTTP 200 OK`).
3. **Jenkins Pipeline Activation**:
   - Stage **Checkout**: Pulls SHA `d4dc7d4`.
   - Stage **Build**: Compiles 35 source classes with Java 21.
   - Stage **Unit Tests**: 39 unit tests pass in 0.8s.
   - Stage **Integration Tests**: Consistency and relational tests pass.
   - Stage **API Tests**: Authentication, print job, queue, and admin controllers pass.
   - Stage **E2E & Smoke Tests**: Complete user journey (Register ➔ Submit ➔ Track ➔ Cancel ➔ Admin Retry) passes.
   - Stage **Quality Gate**: Confirms 118 passing tests; Surefire XML reports published.
   - Stage **Package**: Builds `digital-print-queue-1.0.0.jar`.
   - Stage **Docker Build**: Produces image `digital-print-queue:latest`.
   - Stage **Ansible Deployment**: Mounts `printqueue_uploads`, deploys `.env`, launches container.
   - Stage **Health Check**: Actuator returns `HTTP 200` with `{"status":"UP"}`.
4. **Result**: Status **SUCCESS (Blue / Green)**. Live application is updated seamlessly.

---

## 4. Failure Scenario Demonstration & Deployment Guard

### Step 1: Breaking Code Introduced
A developer pushes a broken assertion or regression bug (e.g. changing priority queue comparator logic or database foreign key cascade rule):
```bash
git commit -m "refactor: altered status transition rules"
git push origin develop
```

### Step 2: Webhook & Build Trigger
- Jenkins automatically starts build `#15`.
- **Checkout** and **Build** complete successfully.

### Step 3: Test Execution Failure
- In **Stage: Integration Tests**, `PrinterStateConsistencyTest` fails:
  ```text
  [ERROR] Failures: 
  [ERROR]   PrinterStateConsistencyTest.testPauseWhenIdleThrows:65 Status expected:<400> but was:<200>
  [INFO] Tests run: 118, Failures: 1, Errors: 0, Skipped: 0
  [ERROR] BUILD FAILURE
  ```

### Step 4: Pipeline Termination & Deployment Guard
- **The build terminates immediately with exit code 1**.
- **Stage Package**: `ABORTED / SKIPPED`.
- **Stage Docker Build**: `ABORTED / SKIPPED` (No broken Docker image is built or tagged).
- **Stage Ansible Deployment**: `ABORTED / SKIPPED` (No containers are replaced or updated).
- **Stage Health Check**: `ABORTED / SKIPPED`.
- **Production Safety**: Existing running production containers remain untouched and continue serving users without disruption!
- Status: **FAILURE (Red)**. Notification sent to developer.

### Step 5: Bug Fix & Recovery
The developer inspects the Surefire test report, rectifies the regression in code, and pushes the fix:
```bash
git commit -m "fix(printer): restore strict FSM transition validation"
git push origin develop
```
- Jenkins triggers build `#16`.
- All 118 tests pass cleanly.
- Quality Gate evaluates to **PASS**.
- Packaging, Docker build, and Ansible deployment proceed and complete with status **SUCCESS**.

---

## 5. Regression Verification Matrix

Every pipeline execution validates that all previously implemented subsystem features continue to function flawlessly:

| Subsystem | Verified Capabilities | Test Class Coverage | Result |
|-----------|------------------------|---------------------|--------|
| **Authentication** | Registration, Login, Logout, BCrypt hashing, JWT tokens, Inactive accounts | `UserServiceTest`, `AuthControllerTest`, `JwtTokenProviderTest` | 🟢 100% PASS |
| **Print Jobs** | File storage, Page calculations, Pricing logic, Copies, Ownership security, Cancellation | `PrintJobServiceTest`, `PrintJobControllerTest`, `PrintQueueE2ETest` | 🟢 100% PASS |
| **Queue Engine** | Multi-tier priority (`HIGH` > `NORMAL` > `LOW`), Temporal FIFO tie-breaking, Dynamic re-indexing | `QueueServiceTest`, `QueueConsistencyTest`, `QueueControllerTest` | 🟢 100% PASS |
| **Virtual Printer** | Non-blocking background simulation, States (`IDLE`, `PRINTING`, `PAUSED`, `ERROR`, `OFFLINE`) | `VirtualPrinterServiceTest`, `PrinterStateConsistencyTest`, `PrinterControllerTest` | 🟢 100% PASS |
| **Admin Controls** | 10-metric system statistics, Global jobs view, User directory, Priority override, Retry failed jobs | `AdminServiceTest`, `AdminControllerTest` | 🟢 100% PASS |
| **Database** | Unique constraints, Foreign keys, Cascade audit logging, Entity mapping | `UserRepositoryTest`, `DatabaseConsistencyTest` | 🟢 100% PASS |
| **REST APIs** | HTTP methods, JSON validation, Status codes (200, 201, 204, 400, 401, 403, 404, 500) | Full Controller Test Suite | 🟢 100% PASS |
| **Frontend UI** | 13 HTML pages, static assets, JS fetch client, live polling | `FrontendPagesIntegrationTest` | 🟢 100% PASS |
