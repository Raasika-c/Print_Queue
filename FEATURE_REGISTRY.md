# FEATURE REGISTRY
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Version**: 1.1 | **Updated**: 2026-09-17

---

### F-001: User Registration
**Module**: Authentication | **Phase**: Phase 1 | **Status**: 🟢 COMPLETE

**Description**: New user self-registration. Name, email, mobile, password collected. BCrypt-12 hashed. Default role: USER. Returns JWT on success.

**Backend Files**: `AuthController.java`, `UserService.java`, `UserRepository.java`, `User.java`, `RegisterRequest.java`, `AuthResponse.java`
**Frontend Files**: `register.html`, `auth.js`
**Database Tables**: `users`
**API Endpoints**: `POST /api/auth/register`
**Tests**: `UserServiceTest` (testRegisterSuccess, testRegisterDuplicateEmail, testNewUserHasUserRoleByDefault, testNewUserHasActiveStatus, testPasswordIsHashed, testRegistrationReturnsJwtToken), `AuthControllerTest` (testRegisterSuccess, testRegisterMissingEmail, testRegisterInvalidEmail, testRegisterShortPassword, testRegisterMissingName, testRegisterDuplicateEmail, testRegisterWeakPassword)
**Business Rules**: BR-001, BR-002, BR-003

---

### F-002: User Login (JWT)
**Module**: Authentication | **Phase**: Phase 1 | **Status**: 🟢 COMPLETE

**Description**: Authenticate with email+password. Returns JWT token (24h). Inactive users rejected. Generic error message always.

**Backend Files**: `AuthController.java`, `UserService.java`, `JwtTokenProvider.java`, `JwtAuthenticationFilter.java`, `LoginRequest.java`
**Frontend Files**: `index.html`, `auth.js`
**Database Tables**: `users`
**API Endpoints**: `POST /api/auth/login`
**Tests**: `UserServiceTest` (testLoginSuccess, testLoginWrongPassword, testLoginInactiveUser, testLoginDoesNotRevealFieldError, testAdminLogin), `AuthControllerTest` (testLoginSuccess, testLoginInvalidCredentials, testLoginMissingEmail, testLoginMissingPassword, testLoginInvalidEmailFormat)
**Business Rules**: BR-004, BR-005

---

### F-003: Get Current User Info
**Module**: Authentication | **Phase**: Phase 1 | **Status**: 🟢 COMPLETE

**Description**: Returns authenticated user's profile from JWT. Password never exposed.

**Backend Files**: `AuthController.java`, `UserService.java`, `UserResponse.java`
**API Endpoints**: `GET /api/auth/me`
**Tests**: `UserServiceTest` (testGetCurrentUserSuccess, testGetCurrentUserNotFound), `AuthControllerTest` (testGetMeAuthenticated, testGetMeUnauthorized)

---

### F-004: User Logout
**Module**: Authentication | **Phase**: Phase 1 | **Status**: 🟢 COMPLETE

**Description**: Stateless JWT logout — instructs client to discard token.

**Backend Files**: `AuthController.java`
**API Endpoints**: `POST /api/auth/logout`
**Tests**: `AuthControllerTest` (testLogoutSuccess)

---

### F-025: Health Monitoring
**Module**: Operations | **Phase**: Phase 1 | **Status**: 🟢 COMPLETE

**Description**: Spring Boot Actuator health endpoint. Reports app + DB status.

**Backend Files**: `application.properties` (actuator config)
**API Endpoints**: `GET /actuator/health`

---

### F-005: File Upload
**Module**: File Management | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-006: Print Job Submission
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-007: View Own Print Jobs
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-008: View Job Details
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-009: Cancel Print Job
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-010: Cost Calculation
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-011: Job Number Generation
**Module**: Print Job | **Phase**: Phase 2 | **Status**: 🔴 NOT STARTED

### F-012: Queue Management Engine
**Module**: Queue | **Phase**: Phase 4 | **Status**: 🟢 COMPLETE

### F-013: Virtual Printer Simulation
**Module**: Printer | **Phase**: Phase 5 | **Status**: 🟢 COMPLETE

### F-014: Admin View All Jobs
**Module**: Admin | **Phase**: Phase 6 | **Status**: 🟢 COMPLETE

### F-015: Admin Change Job Status
**Module**: Admin | **Phase**: Phase 6 | **Status**: 🟢 COMPLETE

### F-016: Admin Printer Management
**Module**: Admin | **Phase**: Phase 5 | **Status**: 🟢 COMPLETE

### F-017: Admin Queue Control
**Module**: Admin | **Phase**: Phase 6 | **Status**: 🟢 COMPLETE

### F-018: Audit Logging
**Module**: Audit | **Phase**: Phase 6 | **Status**: 🟢 COMPLETE

### F-019: Admin Statistics Dashboard
**Module**: Admin | **Phase**: Phase 6 | **Status**: 🟢 COMPLETE

### F-020: User Dashboard UI
**Module**: Frontend | **Phase**: Phase 7 | **Status**: 🟢 COMPLETE

### F-021: Admin Dashboard UI
**Module**: Frontend | **Phase**: Phase 7 | **Status**: 🟢 COMPLETE

### F-022: Docker Containerization
**Module**: DevOps | **Phase**: Phase 6 | **Status**: 🔴 NOT STARTED

### F-023: Jenkins CI Pipeline
**Module**: DevOps | **Phase**: Phase 6 | **Status**: 🔴 NOT STARTED

### F-024: Ansible Deployment Automation
**Module**: DevOps | **Phase**: Phase 7 | **Status**: 🔴 NOT STARTED

---

## Summary Table

| Feature ID | Feature Name | Module | Phase | Status |
|-----------|-------------|--------|-------|--------|
| F-001 | User Registration | Authentication | 1 | 🟢 COMPLETE |
| F-002 | User Login (JWT) | Authentication | 1 | 🟢 COMPLETE |
| F-003 | Get Current User Info | Authentication | 1 | 🟢 COMPLETE |
| F-004 | User Logout | Authentication | 1 | 🟢 COMPLETE |
| F-005 | File Upload | File Management | 2 | 🔴 NOT STARTED |
| F-006 | Print Job Submission | Print Job | 2 | 🔴 NOT STARTED |
| F-007 | View Own Print Jobs | Print Job | 2 | 🔴 NOT STARTED |
| F-008 | View Job Details | Print Job | 2 | 🔴 NOT STARTED |
| F-009 | Cancel Print Job | Print Job | 2 | 🔴 NOT STARTED |
| F-010 | Cost Calculation | Print Job | 2 | 🔴 NOT STARTED |
| F-011 | Job Number Generation | Print Job | 2 | 🔴 NOT STARTED |
| F-013 | Virtual Printer Simulation | Printer | 3 | 🔴 NOT STARTED |
| F-014 | Admin View All Jobs | Admin | 4 | 🔴 NOT STARTED |
| F-015 | Admin Change Job Status | Admin | 4 | 🔴 NOT STARTED |
| F-016 | Admin Printer Management | Admin | 4 | 🔴 NOT STARTED |
| F-017 | Admin Queue Control | Admin | 4 | 🔴 NOT STARTED |
| F-018 | Audit Logging | Audit | 4 | 🔴 NOT STARTED |
| F-019 | Admin Statistics Dashboard | Admin | 4 | 🔴 NOT STARTED |
| F-020 | User Dashboard UI | Frontend | 5 | 🔴 NOT STARTED |
| F-021 | Admin Dashboard UI | Frontend | 5 | 🔴 NOT STARTED |
| F-022 | Docker Containerization | DevOps | 6 | 🔴 NOT STARTED |
| F-023 | Jenkins CI Pipeline | DevOps | 6 | 🔴 NOT STARTED |
| F-024 | Ansible Deployment Automation | DevOps | 7 | 🔴 NOT STARTED |
| F-025 | Health Monitoring | Operations | 1 | 🟢 COMPLETE |

---

## Phase Completion Tracker

| Phase | Features | Status |
|-------|----------|--------|
| Phase 0 | System Design | 🟢 COMPLETE |
| Phase 1 | Foundation + Auth (F-001,002,003,004,025) | 🟢 COMPLETE |
| Phase 2 | Print Job & Queue (F-005 to F-011) | 🔴 NOT STARTED |
| Phase 3 | Printer Simulation (F-012, F-013) | 🔴 NOT STARTED |
| Phase 4 | Admin & Reporting (F-014 to F-019) | 🔴 NOT STARTED |
| Phase 5 | Frontend Polish (F-020, F-021) | 🔴 NOT STARTED |
| Phase 6 | Docker & CI/CD (F-022, F-023) | 🔴 NOT STARTED |
| Phase 7 | Ansible (F-024) | 🔴 NOT STARTED |
| Phase 8 | Final QA | 🔴 NOT STARTED |
