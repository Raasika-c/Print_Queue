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
| F-005 | File Upload | File Management | 3 | 🟢 COMPLETE |
| F-006 | Print Job Submission | Print Job | 3 | 🟢 COMPLETE |
| F-007 | View Own Print Jobs | Print Job | 3 | 🟢 COMPLETE |
| F-008 | View Job Details | Print Job | 3 | 🟢 COMPLETE |
| F-009 | Cancel Print Job | Print Job | 3 | 🟢 COMPLETE |
| F-010 | Cost Calculation | Print Job | 3 | 🟢 COMPLETE |
| F-011 | Job Number Generation | Print Job | 3 | 🟢 COMPLETE |
| F-012 | Queue Management Engine | Queue | 4 | 🟢 COMPLETE |
| F-013 | Virtual Printer Simulation | Printer | 5 | 🟢 COMPLETE |
| F-014 | Admin View All Jobs | Admin | 6 | 🟢 COMPLETE |
| F-015 | Admin Change Job Status | Admin | 6 | 🟢 COMPLETE |
| F-016 | Admin Printer Management | Admin | 6 | 🟢 COMPLETE |
| F-017 | Admin Queue Control | Admin | 6 | 🟢 COMPLETE |
| F-018 | Audit Logging | Audit | 6 | 🟢 COMPLETE |
| F-019 | Admin Statistics Dashboard | Admin | 6 | 🟢 COMPLETE |
| F-020 | User Dashboard UI | Frontend | 7 | 🟢 COMPLETE |
| F-021 | Admin Dashboard UI | Frontend | 7 | 🟢 COMPLETE |
| F-022 | Docker Containerization | DevOps | 12 | 🟢 COMPLETE |
| F-023 | Jenkins CI Pipeline | DevOps | 10 | 🟢 COMPLETE |
| F-024 | Ansible Deployment Automation | DevOps | 13 | 🟢 COMPLETE |
| F-025 | Health Monitoring | Operations | 1 | 🟢 COMPLETE |

---

## Phase Completion Tracker

| Phase | Description | Status |
|-------|-------------|--------|
| Phase 0 | Complete System Design | 🟢 COMPLETE |
| Phase 1 | Spring Boot Foundation | 🟢 COMPLETE |
| Phase 2 | Database & Authentication | 🟢 COMPLETE |
| Phase 3 | Print Job Management | 🟢 COMPLETE |
| Phase 4 | Digital Print Queue Engine | 🟢 COMPLETE |
| Phase 5 | Virtual Printer Simulation | 🟢 COMPLETE |
| Phase 6 | Admin Management & Job History | 🟢 COMPLETE |
| Phase 7 | Complete Frontend & E2E | 🟢 COMPLETE |
| Phase 8 | Automated QA & Regression Testing | 🟢 COMPLETE |
| Phase 9 | Version Control Implementation | 🟢 COMPLETE |
| Phase 10 | Jenkins CI/CD Pipeline | 🟢 COMPLETE |
| Phase 11 | GitHub Webhook Integration | 🟢 COMPLETE |
| Phase 12 | Docker Containerization | 🟢 COMPLETE |
| Phase 13 | Ansible Automation | 🟢 COMPLETE |

