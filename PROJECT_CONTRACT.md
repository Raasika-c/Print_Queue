# PROJECT CONTRACT
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Version**: 1.1 | **Last Updated**: 2026-09-17 | **Status**: APPROVED FOR IMPLEMENTATION

---

## 1. Project Identity

| Field | Value |
|-------|-------|
| Project Name | Digital Printing Queue Management System |
| Course | 23IT723 – DevOps Laboratory |
| Academic Year | 2026–2027 |
| Version | 1.0.0 |
| Artifact ID | digital-print-queue |
| Group ID | com.printqueue |

---

## 2. Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Browser / HTTP Client                      │
│              HTML + CSS + Vanilla JS Frontend               │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP/REST (JWT Bearer token)
┌──────────────────────────▼──────────────────────────────────┐
│           Spring Boot Application (Port 8080)               │
│  ┌──────────────┐  ┌───────────────┐  ┌───────────────────┐ │
│  │  Controllers │  │   Services    │  │  Scheduler        │ │
│  │  (REST API)  │  │  (Business)   │  │  (Queue Engine)   │ │
│  └──────────────┘  └───────────────┘  └───────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Spring Security (JWT Filter)               │ │
│  └─────────────────────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │            Spring Data JPA Repositories                 │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────┘
                           │ JDBC
┌──────────────────────────▼──────────────────────────────────┐
│              MySQL 8.0 Database (Port 3306)                 │
│  users | print_jobs | printers | audit_logs | app_settings  │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 21 (LTS) |
| Framework | Spring Boot | 3.3.4 |
| Security | Spring Security + jjwt | 6.x + 0.12.6 |
| ORM | Spring Data JPA + Hibernate | 6.x |
| Database | MySQL | 8.0 |
| Migration | Flyway | 10.x |
| Build | Apache Maven | 3.9.16 |
| Testing | JUnit 5 + Mockito | 5.x + 5.x |
| API Docs | SpringDoc OpenAPI | 2.6.0 |
| Frontend | HTML + CSS + Vanilla JS | — |
| Containers | Docker + Docker Compose | Latest |
| CI/CD | Jenkins | 2.x (WAR) |
| Config Mgmt | Ansible | Latest |
| VCS | Git + GitHub | — |

---

## 4. User Entity Definition

| Field | Type | Constraints |
|-------|------|-------------|
| id | Long (BIGINT) | PK, AUTO_INCREMENT |
| name | String (VARCHAR 100) | NOT NULL, 2-100 chars |
| email | String (VARCHAR 150) | NOT NULL, UNIQUE, valid email format |
| mobile | String (VARCHAR 10) | NULLABLE, 10 digits exactly |
| password | String (VARCHAR 255) | NOT NULL, BCrypt hashed (NEVER plain text) |
| role | Enum (USER/ADMIN) | NOT NULL, DEFAULT USER |
| status | Enum (ACTIVE/INACTIVE) | NOT NULL, DEFAULT ACTIVE |
| createdAt | LocalDateTime | AUTO, NOT NULL |
| updatedAt | LocalDateTime | AUTO, NOT NULL |

---

## 5. API Contracts

### POST /api/auth/register
- **Auth**: None (public)
- **Request**: `{ "name": "string", "email": "string", "mobile": "string?", "password": "string" }`
- **Success**: `201 Created` with `{ "token", "tokenType", "userId", "name", "email", "role", "status", "message" }`
- **Errors**: `400` (duplicate email, validation failure)

### POST /api/auth/login
- **Auth**: None (public)
- **Request**: `{ "email": "string", "password": "string" }`
- **Success**: `200 OK` with `{ "token", "tokenType", "userId", "name", "email", "role", "status", "message" }`
- **Errors**: `400` (validation), `401` (wrong credentials)

### POST /api/auth/logout
- **Auth**: Any authenticated user
- **Success**: `200 OK` with `{ "message": "Logout successful..." }`

### GET /api/auth/me
- **Auth**: Bearer JWT (any authenticated user)
- **Success**: `200 OK` with UserResponse (no password)
- **Errors**: `401` (not authenticated)

### GET /actuator/health
- **Auth**: Public
- **Success**: `200 OK` with `{ "status": "UP" }`

---

## 6. Authentication Rules

- **API auth**: JWT Bearer token in `Authorization` header
- **Token expiry**: 24 hours (86400000 ms)
- **Password hashing**: BCrypt, strength 12
- **Login failure**: Always returns "Invalid credentials" — never reveals which field failed (BR-005)
- **JWT Secret**: From environment variable `JWT_SECRET` — minimum 32 characters
- **INACTIVE user login**: Rejected with same generic message (BR-004)

---

## 7. Authorization Rules

| Action | USER | ADMIN |
|--------|------|-------|
| Register/Login | ✅ | ✅ |
| View own jobs | ✅ | ✅ |
| Submit print job | ✅ | ✅ |
| Cancel own QUEUED job | ✅ | ✅ |
| View other users' jobs | ❌ | ✅ |
| Access /api/admin/** | ❌ | ✅ |
| Manage printers | ❌ | ✅ |
| Pause/resume queue | ❌ | ✅ |
| View audit logs | ❌ | ✅ |
| Delete audit logs | ❌ | ❌ (nobody) |

---

## 8. Business Rules

| Rule | Description |
|------|-------------|
| BR-001 | Email must be globally unique across all users |
| BR-002 | Password min 8 chars, must contain uppercase, lowercase, digit, special char |
| BR-003 | New registrations always get role=USER |
| BR-004 | INACTIVE users cannot log in |
| BR-005 | Login errors must use generic "Invalid credentials" message |
| BR-006 | Page count: minimum 1, maximum 500 |
| BR-007 | Copies: minimum 1, maximum 50 |
| BR-008 | File must be PDF, JPG, JPEG, or PNG |
| BR-009 | File max size: 10MB |
| BR-010 | Files stored in /app/uploads/{userId}/{jobNumber}/{filename} |
| BR-011 | Filenames are sanitized to prevent path traversal attacks |
| BR-012 | Job number format: PJ-YYYYMMDD-NNNN (daily sequential, resets at midnight) |
| BR-013 | COLOR cost: Rs. 5.00 per page × copies |
| BR-014 | BLACK_WHITE cost: Rs. 1.00 per page × copies |
| BR-015 | Queue ordering: Priority first (HIGH > NORMAL > LOW), then strict FIFO by submitted_at ASC |
| BR-016 | Queue position recalculated when job completes, is cancelled, or priority changes |
| BR-017 | Printer assignment: IDLE printers only, ordered by printer ID (A before B before C) |
| BR-018 | Queue paused: no new jobs assigned to printers |
| BR-019 | Users can only view their own print jobs |
| BR-020 | Job cancellation only allowed when status = QUEUED |
| BR-021 | PROCESSING / COMPLETED / FAILED jobs cannot be cancelled |
| BR-022 | Printer processing time: configured seconds per page (default: 1 sec/page) |
| BR-023 | OFFLINE printers cannot receive print jobs |
| BR-024 | Admin can set IDLE → OFFLINE or OFFLINE → IDLE |
| BR-025 | BUSY printer cannot be set OFFLINE; admin must wait for job completion |

---

## 9. Queue Rules

- Ordered by Priority (`HIGH` > `NORMAL` > `LOW`), then `submitted_at ASC` (oldest job first)
- Queue positions are integers starting from 1
- Position recalculated whenever a job completes, is cancelled, or inserted
- When queue is paused: jobs remain QUEUED, no assignment
- When queue is resumed: scheduler immediately tries assignment

---

## 10. Printer State Rules

```
IDLE ──────────────────► BUSY  (scheduler assigns job)
BUSY ──────────────────► IDLE  (job completes)
IDLE ──────────────────► OFFLINE (admin action)
OFFLINE ───────────────► IDLE  (admin action)
BUSY ──────────────────► OFFLINE  ❌ BLOCKED
```

---

## 11. Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| DB_HOST | MySQL host | localhost | No |
| DB_PORT | MySQL port | 3306 | No |
| DB_NAME | Database name | printqueue_db | No |
| DB_USERNAME | DB username | printqueue_user | No |
| DB_PASSWORD | DB password | printqueue_pass | **YES** |
| JWT_SECRET | JWT signing secret (min 32 chars) | (dev default) | **YES** |
| JWT_EXPIRY_MS | Token validity in ms | 86400000 | No |
| APP_PORT | Application port | 8080 | No |
| FILE_UPLOAD_DIR | Upload directory path | ./uploads | No |
| PRINTER_COUNT | Number of virtual printers | 3 | No |
| PROCESSING_SECONDS_PER_PAGE | Simulated processing time | 1 | No |
| SCHEDULER_INTERVAL_MS | Queue scheduler interval | 2000 | No |

---

## 12. Ports

| Service | Port |
|---------|------|
| Spring Boot App | 8080 |
| MySQL | 3306 |
| Jenkins | 8081 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

---

## 13. Demo Credentials

> ⚠️ **DEMO ONLY — Academic use. Change before any production deployment.**

| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@digitalprint.com | Admin@123 |

---

## 14. Document Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-09-17 | Phase 0: Initial system design |
| 1.1 | 2026-09-17 | Phases 1–13: Core system, frontend, testing, Docker, Jenkins, Ansible |
| 1.2 | 2026-09-17 | Phase 14: Complete Integrated DevOps System & Quality Gate |
| 2.0 | 2026-09-17 | Phase 15: Final Project Validation, 120-Test Audit & Practical Prep |

---

**STATUS: ALL PHASES 0–15 COMPLETE & CERTIFIED (100% QUALITY GATE PASSED)**
