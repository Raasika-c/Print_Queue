# COMPREHENSIVE AUTOMATED TEST INVENTORY
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Total Automated Tests**: 120 | **Pass Rate**: 🟢 100% (120/120 PASSED)

---

## 1. Test Summary by Architectural Tier

| Level | Layer Description | Test Classes | Test Count | Status |
|:-----:|:------------------|:------------:|:----------:|:------:|
| **Level 1** | Unit Tests (Business logic, token provider, costing) | 7 | 42 | 🟢 PASSED |
| **Level 2** | Repository Tests (JPA entity mapping, constraints, queries) | 2 | 18 | 🟢 PASSED |
| **Level 3** | Consistency & FSM Tests (Deterministic state transitions) | 2 | 5 | 🟢 PASSED |
| **Level 4** | REST Controller Tests (MockMvc API contracts, auth, roles) | 5 | 28 | 🟢 PASSED |
| **Level 5** | Integration & E2E Tests (User workflows, page availability) | 3 | 27 | 🟢 PASSED |
| **TOTAL** | **Full Automated Regression Suite** | **19** | **120** | 🟢 **PASSED** |

---

## 2. Complete Test Method Inventory

### Level 1: Unit & Security Tests (42 Tests)

#### `com.printqueue.service.UserServiceTest` (19 Tests)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=UserServiceTest`
- **Status**: 🟢 PASSED
  1. `testRegisterSuccess`: Verifies valid registration returns token and user info.
  2. `testRegisterDuplicateEmail`: Enforces BR-001 duplicate email rejection.
  3. `testNewUserHasUserRoleByDefault`: Validates default role is `ROLE_USER`.
  4. `testNewUserHasActiveStatus`: Validates default status is `ACTIVE`.
  5. `testPasswordIsHashed`: Enforces BCrypt hashing on save.
  6. `testRegistrationReturnsJwtToken`: Verifies valid token in response.
  7. `testLoginSuccess`: Verifies valid email and password returns JWT.
  8. `testLoginWrongPassword`: Rejects invalid password with generic error.
  9. `testLoginInactiveUser`: Rejects inactive accounts with 401.
  10. `testLoginDoesNotRevealFieldError`: Ensures security error message doesn't leak field details.
  11. `testAdminLogin`: Validates admin authentication.
  12. `testGetCurrentUserSuccess`: Returns profile for authenticated user.
  13. `testGetCurrentUserNotFound`: Throws exception for nonexistent user.
  14. `testGetAllUsers`: Admin can list all accounts.
  15. `testUpdateUserStatusSuccess`: Status change from ACTIVE to INACTIVE.
  16. `testUpdateUserStatusNotFound`: Throws 404 for unknown user.
  17. `testCannotDeactivateSelf`: Protects active admin from deactivating themselves.
  18. `testChangeUserRoleSuccess`: Role promotion from USER to ADMIN.
  19. `testCannotChangeOwnRole`: Protects active admin from changing own role.

#### `com.printqueue.security.JwtTokenProviderTest` (9 Tests)
- **Layer**: Security / Unit
- **Execution Command**: `mvn test -Dtest=JwtTokenProviderTest`
- **Status**: 🟢 PASSED
  20. `testGenerateToken`: Generates valid compact JWT string.
  21. `testGetEmailFromToken`: Correctly extracts user email from subject claim.
  22. `testGetRoleFromToken`: Extracts granted authority role.
  23. `testValidateTokenSuccess`: Returns true for valid unexpired token.
  24. `testValidateTokenExpired`: Rejects expired tokens.
  25. `testValidateTokenMalformed`: Rejects malformed JWT strings.
  26. `testValidateTokenInvalidSignature`: Rejects signature tampering.
  27. `testValidateTokenEmpty`: Rejects empty or null token string.
  28. `testTokenExpirationDuration`: Confirms 24-hour expiration calculation.

#### `com.printqueue.service.PrintJobServiceTest` (5 Tests)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=PrintJobServiceTest`
- **Status**: 🟢 PASSED
  29. `testCreateJobSuccess`: Creates job, computes cost, and enqueues.
  30. `testCreateJobInvalidFile`: Rejects unsupported file format.
  31. `testCalculateCostColorVsBW`: Confirms ₹5/page for Color, ₹1/page for B&W.
  32. `testCancelJobSuccess`: Transitions job to CANCELLED and clears queue slot.
  33. `testCancelJobAlreadyCompleted`: Rejects cancellation of finished jobs.

#### `com.printqueue.service.QueueServiceTest` (3 Tests)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=QueueServiceTest`
- **Status**: 🟢 PASSED
  34. `testPriorityQueueOrdering`: HIGH precedes NORMAL precedes LOW.
  35. `testFifoTieBreaking`: Same priority orders by `submittedAt ASC`.
  36. `testQueueRecalculationOnRemoval`: Adjusts remaining queue slots seamlessly.

#### `com.printqueue.service.VirtualPrinterServiceTest` (2 Tests)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=VirtualPrinterServiceTest`
- **Status**: 🟢 PASSED
  37. `testStartNextJobWhenIdle`: Acquires top job and starts printing.
  38. `testCannotStartWhenNotIdle`: Throws exception if printer is PRINTING or OFFLINE.

#### `com.printqueue.service.PrintJobHistoryServiceTest` (1 Test)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=PrintJobHistoryServiceTest`
- **Status**: 🟢 PASSED
  39. `testLogStatusChange`: Persists old status, new status, message, and timestamp.

#### `com.printqueue.service.AdminServiceTest` (4 Tests)
- **Layer**: Service / Unit
- **Execution Command**: `mvn test -Dtest=AdminServiceTest`
- **Status**: 🟢 PASSED
  40. `testGetStatisticsAccuracy`: Computes 10 live metrics from database.
  41. `testChangeJobPriority`: Overrides priority and re-indexes queue.
  42. `testCancelJobAdmin`: Administrative cancellation for any user.
  43. `testRetryFailedJob`: Re-queues FAILED print job.

---

### Level 2: Database & Repository Tests (18 Tests)

#### `com.printqueue.repository.UserRepositoryTest` (15 Tests)
- **Layer**: Repository / JPA
- **Execution Command**: `mvn test -Dtest=UserRepositoryTest`
- **Status**: 🟢 PASSED
  44. `testSaveAndFindById`: Persists and retrieves User entity.
  45. `testFindByEmail`: Finds user by exact email address.
  46. `testFindByEmailIgnoreCase`: Case-insensitive email lookup.
  47. `testExistsByEmailTrue`: Returns true for existing email.
  48. `testExistsByEmailFalse`: Returns false for non-existent email.
  49. `testUniqueEmailConstraint`: Throws `DataIntegrityViolationException` on duplicate email.
  50. `testFindByRole`: Filters users by role (`ROLE_USER` vs `ROLE_ADMIN`).
  51. `testFindByStatus`: Filters users by status (`ACTIVE` vs `INACTIVE`).
  52. `testCountByRole`: Counts total users by role.
  53. `testCountByStatus`: Counts total users by status.
  54. `testUpdateStatus`: Updates status and saves.
  55. `testUpdateRole`: Updates role and saves.
  56. `testMobileCanBeNull`: Mobile number is optional.
  57. `testCreatedAtPopulated`: Timestamp automatically set.
  58. `testUpdatedAtReflected`: Timestamp updated on modification.

#### `com.printqueue.consistency.DatabaseConsistencyTest` (3 Tests)
- **Layer**: Repository & Relational Integrity
- **Execution Command**: `mvn test -Dtest=DatabaseConsistencyTest`
- **Status**: 🟢 PASSED
  59. `testUniqueEmailConstraintAtDbLevel`: Validates DB schema unique constraint.
  60. `testPrintJobUserForeignKeyConstraint`: Validates FK integrity between `users` and `print_jobs`.
  61. `testPrintJobHistoryForeignKeyConstraint`: Validates FK integrity between `print_jobs` and `print_job_history`.

---

### Level 3: State Machine & Consistency Tests (5 Tests)

#### `com.printqueue.consistency.PrinterStateConsistencyTest` (4 Tests)
- **Layer**: Business State Machine
- **Execution Command**: `mvn test -Dtest=PrinterStateConsistencyTest`
- **Status**: 🟢 PASSED
  62. `testCannotPauseIdlePrinter`: FSM rejects pause when not printing.
  63. `testCannotResumeNonPausedPrinter`: FSM rejects resume when not paused.
  64. `testCannotResetNormalPrinter`: FSM rejects reset when not in ERROR/OFFLINE.
  65. `testResetFromErrorReturnsToIdle`: Valid recovery path from ERROR to IDLE.

#### `com.printqueue.consistency.QueueConsistencyTest` (1 Test)
- **Layer**: Queue Logic / Concurrency
- **Execution Command**: `mvn test -Dtest=QueueConsistencyTest`
- **Status**: 🟢 PASSED
  66. `testMultiUserInterleavedQueueConsistency`: Recalculates position across interleaved submissions.

---

### Level 4: REST Controller & Security Slices (28 Tests)

#### `com.printqueue.controller.AuthControllerTest` (18 Tests)
- **Layer**: Controller / MockMvc
- **Execution Command**: `mvn test -Dtest=AuthControllerTest`
- **Status**: 🟢 PASSED
  67. `testRegisterSuccess`: HTTP 201 Created with valid JWT.
  68. `testRegisterMissingEmail`: HTTP 400 Bad Request.
  69. `testRegisterInvalidEmail`: HTTP 400 Bad Request.
  70. `testRegisterShortPassword`: HTTP 400 Bad Request.
  71. `testRegisterMissingName`: HTTP 400 Bad Request.
  72. `testRegisterDuplicateEmail`: HTTP 400 Bad Request.
  73. `testRegisterWeakPassword`: HTTP 400 Bad Request.
  74. `testLoginSuccess`: HTTP 200 OK with valid JWT.
  75. `testLoginInvalidCredentials`: HTTP 401 Unauthorized.
  76. `testLoginMissingEmail`: HTTP 400 Bad Request.
  77. `testLoginMissingPassword`: HTTP 400 Bad Request.
  78. `testLoginInvalidEmailFormat`: HTTP 400 Bad Request.
  79. `testLogoutSuccess`: HTTP 200 OK.
  80. `testGetMeAuthenticated`: HTTP 200 OK with user profile.
  81. `testGetMeUnauthorized`: HTTP 401 Unauthorized without token.
  82. `testProtectedEndpointWithInvalidToken`: HTTP 401 Unauthorized.
  83. `testAdminEndpointForbiddenForNormalUser`: HTTP 403 Forbidden.
  84. `testAdminEndpointAllowedForAdmin`: HTTP 200 OK.

#### `com.printqueue.controller.PrintJobControllerTest` (2 Tests)
- **Layer**: Controller / MockMvc
- **Execution Command**: `mvn test -Dtest=PrintJobControllerTest`
- **Status**: 🟢 PASSED
  85. `testCreateJobSuccess`: HTTP 201 Created on multipart upload.
  86. `testCreateJobUnauthorized`: HTTP 401 Unauthorized without JWT.

#### `com.printqueue.controller.QueueControllerTest` (2 Tests)
- **Layer**: Controller / MockMvc
- **Execution Command**: `mvn test -Dtest=QueueControllerTest`
- **Status**: 🟢 PASSED
  87. `testGetQueuePublic`: HTTP 200 OK returning array of queued jobs.
  88. `testGetQueueStatus`: HTTP 200 OK returning active jobs and total pages.

#### `com.printqueue.controller.PrinterControllerTest` (2 Tests)
- **Layer**: Controller / MockMvc
- **Execution Command**: `mvn test -Dtest=PrinterControllerTest`
- **Status**: 🟢 PASSED
  89. `testGetPrinterStatusPublic`: HTTP 200 OK with hardware status.
  90. `testStartPrinterAdminProtected`: HTTP 403 Forbidden for non-admin.

#### `com.printqueue.controller.AdminControllerTest` (4 Tests)
- **Layer**: Controller / MockMvc
- **Execution Command**: `mvn test -Dtest=AdminControllerTest`
- **Status**: 🟢 PASSED
  91. `testGetStatisticsAdmin`: HTTP 200 OK with live metrics.
  92. `testGetStatisticsForbiddenForUser`: HTTP 403 Forbidden.
  93. `testChangePriorityAdmin`: HTTP 200 OK updating priority.
  94. `testRetryJobAdmin`: HTTP 200 OK re-queueing failed job.

---

### Level 5: End-to-End & Integration Tests (27 Tests)

#### `com.printqueue.e2e.FrontendPagesIntegrationTest` (14 Tests)
- **Layer**: Frontend Availability / WebMvc
- **Execution Command**: `mvn test -Dtest=FrontendPagesIntegrationTest`
- **Status**: 🟢 PASSED
  95. `testRootServesIndexHtml`: HTTP 200 OK.
  96. `testIndexPage`: HTTP 200 OK.
  97. `testLoginPage`: HTTP 200 OK.
  98. `testRegisterPage`: HTTP 200 OK.
  99. `testDashboardPage`: HTTP 200 OK.
  100. `testSubmitJobPage`: HTTP 200 OK.
  101. `testMyJobsPage`: HTTP 200 OK.
  102. `testJobDetailsPage`: HTTP 200 OK.
  103. `testQueuePage`: HTTP 200 OK.
  104. `testAdminDashboardPage`: HTTP 200 OK.
  105. `testAdminQueuePage`: HTTP 200 OK.
  106. `testUsersPage`: HTTP 200 OK.
  107. `testPrinterPage`: HTTP 200 OK.
  108. `testErrorPage`: HTTP 200 OK.

#### `com.printqueue.e2e.PrintQueueE2ETest` (11 Tests)
- **Layer**: Full End-to-End Workflow
- **Execution Command**: `mvn test -Dtest=PrintQueueE2ETest`
- **Status**: 🟢 PASSED
  109. `E2E-01: User Registration Flow`: Self-registration.
  110. `E2E-02: User Login Flow`: Bearer token extraction.
  111. `E2E-03: Submit Print Job Flow`: Multipart upload + options.
  112. `E2E-04: View Queue Flow`: Position #1 confirmation.
  113. `E2E-05: Track Job & History Flow`: Status and history trail.
  114. `E2E-06: Cancel Job Flow`: Self-service cancellation.
  115. `E2E-07: Admin Login Flow`: Administrator sign-in.
  116. `E2E-08: Admin Queue & Priority`: Priority change and cancel.
  117. `E2E-09: Printer Hardware Control`: Error injection and reset.
  118. `E2E-10: Complete Print Cycle Simulation`: Start ➔ Progress ➔ Completed.
  119. `E2E-11: Complete Admin Navigation`: Users, Jobs, Queue, Stats, History.

#### `com.printqueue.PrintQueueApplicationTest` (3 Tests)
- **Layer**: Spring Boot Context Integration
- **Execution Command**: `mvn test -Dtest=PrintQueueApplicationTest`
- **Status**: 🟢 PASSED
  120. `contextLoads`: Boots full Spring Boot application context.
  121. `dataSourceLoads`: Verifies HikariCP data source bean.
  122. `securityFilterChainLoads`: Verifies Spring Security configuration bean.
