# FINAL PROJECT TEST MATRIX
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Quality Gate Status**: 🟢 **100% PASSED (120 Automated Tests + Contract Assertions)**

---

## Complete Quality Assurance Test Matrix

| Test ID | Module | Scenario Tested | Expected Result | Actual Result | Status | Automation Method |
|:--------|:-------|:----------------|:----------------|:--------------|:------:|:------------------|
| **TC-AUTH-001** | Authentication | Valid self-registration | HTTP 201 Created + JWT Token returned | Returned token, role `USER`, status `ACTIVE` | 🟢 PASS | Automated (`UserServiceTest`) |
| **TC-AUTH-002** | Authentication | Duplicate email registration | Enforces BR-001; HTTP 400 Bad Request | Throws `BadRequestException` | 🟢 PASS | Automated (`AuthControllerTest`) |
| **TC-AUTH-003** | Authentication | Password complexity check | Enforces BR-002; rejects weak passwords | Validation fails with HTTP 400 | 🟢 PASS | Automated (`AuthControllerTest`) |
| **TC-AUTH-004** | Authentication | Valid login credentials | HTTP 200 OK + 24-hour Bearer token | Valid compact signed JWT received | 🟢 PASS | Automated (`JwtTokenProviderTest`) |
| **TC-AUTH-005** | Authentication | Wrong login password | Enforces BR-005; generic 401 Unauthorized | HTTP 401 with generic message | 🟢 PASS | Automated (`UserServiceTest`) |
| **TC-AUTH-006** | Authentication | Inactive account login | Rejects login with HTTP 401 | Blocked with HTTP 401 | 🟢 PASS | Automated (`UserServiceTest`) |
| **TC-AUTH-007** | Authentication | Password hashing verification | Passwords never stored in plaintext | BCrypt-12 hash stored (`$2a$12$...`) | 🟢 PASS | Automated (`UserServiceTest`) |
| **TC-AUTH-008** | Authentication | Non-admin accesses admin endpoint | Protected by `@PreAuthorize`; HTTP 403 | Intercepted; returns HTTP 403 Forbidden | 🟢 PASS | Automated (`AdminControllerTest`) |
| **TC-JOB-001** | Print Job | Multipart upload (PDF + JSON options) | HTTP 201 Created; job number generated | Sequential `DPQ-2026-XXXX` assigned | 🟢 PASS | Automated (`PrintJobControllerTest`) |
| **TC-JOB-002** | Print Job | Cost calculation (Color mode) | Cost = ₹5.00 × pages × copies | Dynamic cost ₹10.00 for 2 pgs | 🟢 PASS | Automated (`PrintJobServiceTest`) |
| **TC-JOB-003** | Print Job | Cost calculation (B&W mode) | Cost = ₹1.00 × pages × copies | Dynamic cost ₹2.00 for 2 pgs | 🟢 PASS | Automated (`PrintJobServiceTest`) |
| **TC-JOB-004** | Print Job | View own jobs isolation | Users only see their own submitted jobs | Filtered by authenticated `User` | 🟢 PASS | Automated (`PrintJobServiceTest`) |
| **TC-JOB-005** | Print Job | User cancels own queued job | Status transitions to `CANCELLED` | Job cancelled; queue position cleared | 🟢 PASS | Automated (`PrintQueueE2ETest`) |
| **TC-JOB-006** | Print Job | Cancel already completed job | Rejection with HTTP 400 Bad Request | Throws `BadRequestException` | 🟢 PASS | Automated (`PrintJobServiceTest`) |
| **TC-QUEUE-001**| Queue Engine | Priority ordering (`HIGH` > `NORMAL` > `LOW`) | Higher priority jobs jump ahead in queue | Priority weights (3 > 2 > 1) verified | 🟢 PASS | Automated (`QueueServiceTest`) |
| **TC-QUEUE-002**| Queue Engine | FIFO tie-breaking within same priority | Earliest `submittedAt` gets lower position | Ordered by `submittedAt ASC` | 🟢 PASS | Automated (`QueueServiceTest`) |
| **TC-QUEUE-003**| Queue Engine | Queue recalculation on job cancellation | Subsequent jobs advance by 1 slot | Slots re-indexed (`1, 2, 3...`) | 🟢 PASS | Automated (`QueueServiceTest`) |
| **TC-QUEUE-004**| Queue Engine | Queue recalculation on job completion | Completed job departs queue; next becomes #1 | Position 1 reallocated immediately | 🟢 PASS | Automated (`PrintQueueE2ETest`) |
| **TC-PRINT-001**| Virtual Printer| Start next job when `IDLE` | Transitions to `PRINTING`; starts top job | Printer `PRINTER-01` acquires Job #1 | 🟢 PASS | Automated (`VirtualPrinterServiceTest`) |
| **TC-PRINT-002**| Virtual Printer| Reject start when already `PRINTING` | Only `IDLE` printers can start jobs | Throws `BadRequestException` | 🟢 PASS | Automated (`VirtualPrinterServiceTest`) |
| **TC-PRINT-003**| Virtual Printer| Pause active printing job | Transitions to `PAUSED` | Printer status set to `PAUSED` | 🟢 PASS | Automated (`PrinterStateConsistencyTest`) |
| **TC-PRINT-004**| Virtual Printer| Resume paused printer | Resumes printing simulation thread | Printer status returned to `PRINTING` | 🟢 PASS | Automated (`PrinterStateConsistencyTest`) |
| **TC-PRINT-005**| Virtual Printer| Error injection & recovery reset | `ERROR` ➔ Reset ➔ `IDLE` recovery path | Transitions verified deterministically | 🟢 PASS | Automated (`PrinterStateConsistencyTest`) |
| **TC-PRINT-006**| Virtual Printer| Non-blocking asynchronous simulation | HTTP call returns immediately; background loop | `CompletableFuture` runs in background | 🟢 PASS | Automated (`PrintQueueE2ETest`) |
| **TC-PRINT-007**| Virtual Printer| Page progress simulation to completion | Progress updates page-by-page to 100% | Job reaches `COMPLETED`, progress 100% | 🟢 PASS | Automated (`PrintQueueE2ETest`) |
| **TC-ADMIN-001**| Admin Console | 10-Metric statistics calculation | Exact count calculated from database | Real-time counts match DB state | 🟢 PASS | Automated (`AdminServiceTest`) |
| **TC-ADMIN-002**| Admin Console | Administrative priority override | Admin elevates job to `HIGH` priority | Queue re-indexes immediately | 🟢 PASS | Automated (`PrintQueueE2ETest`) |
| **TC-ADMIN-003**| Admin Console | Admin cancel any print job | Admin can cancel jobs of other users | Status updated to `CANCELLED` | 🟢 PASS | Automated (`AdminServiceTest`) |
| **TC-ADMIN-004**| Admin Console | Retry failed print job | Re-queues FAILED job to position #1 | Status reset to `QUEUED` | 🟢 PASS | Automated (`AdminServiceTest`) |
| **TC-AUDIT-001**| Audit Logging | Lifecycle status transition auditing | Every status change logged in history | Records `oldStatus`, `newStatus`, `changedBy` | 🟢 PASS | Automated (`PrintJobHistoryServiceTest`) |
| **TC-DB-001** | Database | Global unique email constraint | DB rejects duplicate emails via unique index | `DataIntegrityViolationException` | 🟢 PASS | Automated (`DatabaseConsistencyTest`) |
| **TC-DB-002** | Database | User-to-PrintJob foreign key integrity | Reject orphaned print jobs without user | Foreign key constraint enforced | 🟢 PASS | Automated (`DatabaseConsistencyTest`) |
| **TC-DB-003** | Database | PrintJob-to-History foreign key integrity | History entries cascade with job entity | Foreign key constraint enforced | 🟢 PASS | Automated (`DatabaseConsistencyTest`) |
| **TC-UI-001** | Frontend UI | 13 HTML pages availability | All 13 pages serve with HTTP 200 OK | All 13 pages return HTTP 200 OK | 🟢 PASS | Automated (`FrontendPagesIntegrationTest`) |
| **TC-ACT-001** | Actuator | Telemetry health monitoring | `/actuator/health` returns `status: UP` | JSON payload contains `status: UP` | 🟢 PASS | Automated (`PrintQueueApplicationTest`) |
