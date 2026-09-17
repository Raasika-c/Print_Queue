# CHANGELOG
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027

All notable changes are documented here following [Keep a Changelog](https://keepachangelog.com/) format.

---

## [Unreleased]

---

## [1.7.0] — Phase 12 — 2026-09-17

### Phase 12 — Docker Containerization ✅
#### Added
- **Multi-Stage `Dockerfile`**: Multi-stage build separating builder (`maven:3.9.6-eclipse-temurin-21-jammy`) from non-root runtime (`eclipse-temurin:21-jre-jammy`) with health check and memory optimization.
- **Base-Image Comparison Experiment**:
  - `Dockerfile.alpine`: Lightweight musl libc variant (~185 MB).
  - `Dockerfile.ubuntu`: Full glibc compatibility variant (~360 MB).
- **Docker Compose (`docker-compose.yml`)**:
  - Microservice composition with `app` (port 8080) and `mysql:8.0` (port 3306).
  - Configured healthcheck dependencies (`service_healthy`).
  - Persistent volume `printqueue_uploads` ensuring zero data loss across container recreation.
  - Dedicated isolated bridge network `printqueue_network`.
- **`DOCKER.md`**: Complete operational manual documenting all 15 core Docker CLI commands, base-image comparative analysis, persistence verification, and health check validation.

---

## [1.6.0] — Phase 11 — 2026-09-17

### Phase 11 — GitHub Webhook Integration ✅
#### Added
- **`Jenkinsfile` Triggers**: Added `githubPush()` trigger and fallback `pollSCM('H/2 * * * *')` for offline/firewalled lab environments.
- **`GITHUB_WEBHOOK.md`**: Comprehensive lab specification covering:
  - Automated trigger architecture (`git push` ➔ GitHub ➔ Webhook ➔ Jenkins ➔ Pipeline).
  - Detailed explanation of `localhost` limitations in institutional networks.
  - Practical student-lab exposure methods (Ngrok tunnels, Cloudflare tunnels, LocalTunnel, and SCM polling).
  - Step-by-step Jenkins and GitHub configuration.
  - End-to-end verification and quality gate deployment guard walkthrough.
  - Troubleshooting guide for common errors (HTTP 403, trailing slashes, CSRF).

---

## [1.5.0] — Phase 7 — 2026-09-17

### Phase 7 — Complete Frontend & End-to-End Testing ✅
#### Added
- **13 Complete Responsive HTML Pages**:
  - `index.html`: Public landing page with feature overview and real-time live queue snapshot.
  - `login.html`: JWT authentication portal with automatic demo administrator credentials auto-fill.
  - `register.html`: New user registration with password complexity rules enforcement (BR-002).
  - `dashboard.html`: User management dashboard with quick metrics, recent jobs, and navigation.
  - `submit-job.html`: Multi-part document upload with real-time live cost preview (₹5/Color, ₹1/B&W).
  - `my-jobs.html`: User's personal job history with status filtering, detail views, and cancellation modals.
  - `job-details.html`: Granular job inspector with live progress bar, page tracking, specs, and audit trail timeline.
  - `queue.html`: Real-time public/user queue table displaying position, job #, user, priority, and timestamps.
  - `admin-dashboard.html`: Complete 10-metric system statistics monitoring console.
  - `admin-queue.html`: Advanced queue operations (priority adjustments, job cancellation, retry failed jobs).
  - `users.html`: System-wide registered accounts directory with role and status badges.
  - `printer.html`: Dedicated PRINTER-01 virtual hardware console with controls (Start, Pause, Resume, Reset, Error).
  - `error.html`: User-friendly fallback error handling page.
- **Frontend Assets**:
  - `styles.css`: Modern styling complementing Bootstrap 5 with custom animated badges and status indicators.
  - `app.js`: Centralized API fetch layer, JWT session management, toast notification system, and formatters.
- **Backend API Additions**:
  - `GET /api/jobs/{id}/history`: Returns the chronological lifecycle audit trail for a specific print job.
- **End-to-End Tests**:
  - `FrontendPagesIntegrationTest.java`: Validates all 13 HTML pages are served correctly with HTTP 200.
  - `PrintQueueE2ETest.java`: Comprehensive multi-step integration suite covering Registration, Login, Multipart Job Submission, Queue Tracking, Job Cancellation, Admin Management, Priority Updating, and Printer Hardware Simulation.

---

## [1.4.0] — Phase 6 — 2026-09-17

### Phase 6 — Admin Management and Job History ✅
#### Added
- `PrintJobHistory.java` — Entity recording detailed state changes (`oldStatus`, `newStatus`, `message`, `changedBy`).
- `PrintJobHistoryService.java` — Independent auditing hook tracking lifecycle events across `PrintJobService`, `QueueService`, and `VirtualPrinterService`.
- `AdminService.java` — Centralized orchestration for metrics aggregation and cross-user authoritative actions.
- `AdminController.java` — Secured REST API (`/api/admin/**`) granting operations:
  - Global `GET /users` and `GET /jobs`
  - High-level metric aggregation (`GET /statistics`)
  - Elevated permissions (`PUT /priority`, `PUT /retry`, `PUT /cancel`)
- **Security Check**: Enforced `@PreAuthorize("hasRole('ADMIN')")` protecting these APIs from users.
- **Database Migrations**: Included `V5__Phase6_Admin_History.sql` creating the history trail table.

---

## [1.3.0] — Phase 5 — 2026-09-17

### Phase 5 — Virtual Printer and Print Simulation ✅
#### Added
- `VirtualPrinter.java` — Entity representing `PRINTER-01` with states (IDLE, PRINTING, PAUSED, ERROR, OFFLINE).
- `VirtualPrinterService.java` — Handles safe state transitions for the printer. Blocks invalid shifts.
- `PrintingSimulationService.java` — Utilizes non-blocking background threads (`CompletableFuture.runAsync()`) to simulate page-by-page rendering.
- `PrinterController.java` — Admin-secured REST Endpoints: `/status`, `/start`, `/pause`, `/resume`, `/reset`, `/error`.
- **Database Migrations**: Included `V4__Phase5_VirtualPrinter.sql` to reshape the `printers` table safely.
- **Queue Interoperability**: Simulation completion gracefully clears the job from the active queue and auto-advances.

---

## [1.2.0] — Phase 4 — 2026-09-17

### Phase 4 — Digital Print Queue Engine ✅
#### Added
- `QueueService.java` — Core engine managing active job queue with dynamic positional recalculation.
- `QueueController.java` — REST Endpoints: `GET /api/queue` and `GET /api/queue/status`.
- **Queue Algorithm**: Implemented multi-tier sorting (Priority `HIGH` > `NORMAL` > `LOW`, then strict FIFO via `submittedAt`).
- **Dynamic Positioning**: Assures consistent queue positioning numbering. Completed, failed, and cancelled jobs vacate positions gracefully.
- `QueueServiceTest.java` and `QueueControllerTest.java` — Over 10 tests verifying ordering logic, consistency, and positional assignment against concurrent scenarios.

---

## [1.1.0] — Phase 3 — 2026-09-17

### Phase 3 — Print Job Management ✅
#### Added
- `PrintJob.java` — Entity with comprehensive fields: jobNumber, copies, paperSize, colorMode, duplex, priority, progressPercentage, estimatedCost, etc.
- `PrintJobStatus.java` — Enum: SUBMITTED, QUEUED, PRINTING, PAUSED, COMPLETED, CANCELLED, FAILED
- `PaperSize`, `ColorMode`, `Orientation`, `JobPriority` — Enums for job specification
- `PrintJobRepository.java` — Database operations including user-specific lookup and job number generation
- `FileStorageService.java` — Local file system storage for uploaded PDFs/DOCs (with validation for file type and 10MB limit) and page count estimation
- `JobNumberGenerator.java` — Creates sequential daily job numbers (e.g., DPQ-YYYY-0001)
- `CostCalculationService.java` — Enforces pricing: ₹5/page for COLOR, ₹1/page for B&W
- `PrintJobService.java` — Core business logic for job creation, queue insertion, authorization checks, and job cancellation rules (cannot cancel COMPLETED/FAILED)
- `PrintJobController.java` — REST Endpoints:
  - `POST /api/jobs` (multipart upload)
  - `GET /api/jobs` (users see own jobs, admins see all)
  - `GET /api/jobs/{id}` (secured access)
  - `DELETE /api/jobs/{id}` (cancellation)
- `PrintJobRequest`, `PrintJobResponse` — DTOs for data transfer
- `V3__Phase3_PrintJobs.sql` — Flyway migration for updating the `print_jobs` table safely
- `PrintJobServiceTest.java` — Unit tests for service logic
- `PrintJobControllerTest.java` — API tests with MockMvc and mocked security

---

## [1.0.0] — Phase 1 & 2 — 2026-09-17

### Phase 0 — System Design ✅
- Created ARCHITECTURE.md with complete system design
- Created PROJECT_CONTRACT.md (single source of truth)
- Created FEATURE_REGISTRY.md (25 features registered)
- Defined all business rules (BR-001 to BR-025)
- Defined all functional requirements (FR-001 to FR-030)
- Defined complete database schema (5 tables)
- Defined complete REST API contracts (20 endpoints)
- Defined DevOps pipeline: Git → GitHub → Jenkins → Docker → Ansible

### Phase 1 — Spring Boot Foundation ✅
#### Added
- `pom.xml` — Maven project with Java 21, Spring Boot 3.3.4
  - Dependencies: Web, JPA, Security, Validation, Actuator, Thymeleaf
  - JWT: jjwt 0.12.6
  - Database: MySQL 8 connector, H2 (test), Flyway
  - API docs: SpringDoc OpenAPI 2.6.0
  - Utilities: Lombok
- `PrintQueueApplication.java` — Main entry point with `@EnableScheduling`
- `application.properties` — Full configuration with environment variable defaults
- `application-docker.properties` — Docker-specific overrides
- `src/test/resources/application.properties` — H2 in-memory test config
- `.gitignore` — Java, Maven, IDE, Docker, secrets coverage
- `.env.example` — All environment variables documented

### Phase 2 — Database & Authentication ✅
#### Entities Added
- `User.java` — Fields: id, name, email, mobile, password, role, status, createdAt, updatedAt
- `Role.java` — Enum: USER, ADMIN
- `UserStatus.java` — Enum: ACTIVE, INACTIVE

#### Database Migrations Added
- `V1__Initial_Schema.sql` — Creates: users, printers, print_jobs, audit_logs, app_settings tables
- `V2__Seed_Data.sql` — Seeds: 3 virtual printers (A, B, C), app settings with default values

#### Repositories Added
- `UserRepository.java` — findByEmail, existsByEmail, findByRole, findByStatus, countBy*, etc.

#### DTOs Added
- `RegisterRequest.java` — Validated registration form (name, email, mobile, password)
- `LoginRequest.java` — Login credentials (email, password)
- `AuthResponse.java` — Token + user info response
- `UserResponse.java` — User profile (no password exposed)
- `ApiError.java` — Standard error response shape

#### Security Added
- `SecurityConfig.java` — Spring Security, BCrypt-12, stateless JWT, CORS, CSRF disabled
- `JwtTokenProvider.java` — Token generation, parsing, validation (jjwt 0.12.x API)
- `JwtAuthenticationFilter.java` — Extracts and validates Bearer token per request
- `UserDetailsServiceImpl.java` — Loads users by email for Spring Security

#### Services Added
- `UserService.java` — register(), login(), getCurrentUser(), getAllUsers(), updateUserStatus()
  - BR-001: Email uniqueness enforced
  - BR-003: Default role = USER
  - BR-004: INACTIVE users rejected at login
  - BR-005: Generic error message on login failure

#### Controllers Added
- `AuthController.java` — POST /register, POST /login, POST /logout, GET /me

#### Configuration Added
- `DataInitializer.java` — Seeds DEMO admin user on startup
  - ⚠️ DEMO CREDENTIAL: admin@digitalprint.com / Admin@123

#### Exceptions Added
- `GlobalExceptionHandler.java` — Converts all exceptions to standard ApiError JSON
- `ResourceNotFoundException.java` — 404 Not Found
- `BusinessRuleException.java` — 400 Bad Request
- `AuthenticationException.java` — 401 Unauthorized

#### Tests Added
- `PrintQueueApplicationTest.java` — Context load test (Level 4 Integration)
- `UserRepositoryTest.java` — 15 repository tests (Level 2 Repository)
- `UserServiceTest.java` — 18 unit tests (Level 1 Unit)
- `AuthControllerTest.java` — 18 API tests (Level 3 API)
- `JwtTokenProviderTest.java` — 9 JWT tests (Level 1 Unit)

#### Documentation Added
- `README.md` — Setup, API overview, Docker, CI/CD info
- `PROJECT_CONTRACT.md` — Single source of truth (Phase 1 & 2)
- `FEATURE_REGISTRY.md` — All 25 features registered
- `TESTING.md` — Testing strategy, test levels, commands
- `CHANGELOG.md` — This file

### Security Notes
- Passwords hashed with BCrypt (strength 12)
- JWT tokens valid 24 hours
- Admin credential is DEMO only — must change before production
- No plain-text credentials stored anywhere
