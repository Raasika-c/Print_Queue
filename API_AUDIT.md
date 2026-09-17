# API CONTRACT AUDIT REPORT
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Status**: 🟢 **ALL ENDPOINTS AUDITED & VERIFIED (21 APIs)**

---

## 1. Complete REST API Inventory & Contract Specification

| # | HTTP Method | Endpoint URL | Request Payload | Response Payload | HTTP Status | Authentication | Role Required | Automated Test Coverage |
|:--|:------------|:-------------|:----------------|:-----------------|:-----------:|:--------------:|:-------------:|:------------------------|
| **1** | `POST` | `/api/auth/register` | `RegisterRequest` (JSON) | `AuthResponse` (JSON) | `201 Created` | None (Public) | Anyone | `AuthControllerTest#testRegisterSuccess`, `PrintQueueE2ETest#E2E-01` |
| **2** | `POST` | `/api/auth/login` | `LoginRequest` (JSON) | `AuthResponse` (JSON) | `200 OK` | None (Public) | Anyone | `AuthControllerTest#testLoginSuccess`, `PrintQueueE2ETest#E2E-02` |
| **3** | `POST` | `/api/auth/logout` | None | `{"message": "Logged out"}` | `200 OK` | Any | Any | `AuthControllerTest#testLogoutSuccess` |
| **4** | `GET` | `/api/auth/me` | None | `UserResponse` (JSON) | `200 OK` | Bearer JWT | `USER` / `ADMIN` | `AuthControllerTest#testGetMeAuthenticated` |
| **5** | `POST` | `/api/jobs` | Multipart: `file` + `jobDetails` (JSON) | `PrintJobResponse` (JSON) | `201 Created` | Bearer JWT | `USER` / `ADMIN` | `PrintJobControllerTest`, `PrintQueueE2ETest#E2E-03` |
| **6** | `GET` | `/api/jobs` | None | `List<PrintJobResponse>` | `200 OK` | Bearer JWT | `USER` / `ADMIN` | `PrintJobControllerTest`, `PrintQueueE2ETest#E2E-03` |
| **7** | `GET` | `/api/jobs/{id}` | Path: `id` | `PrintJobResponse` (JSON) | `200 OK` | Bearer JWT | `USER` (Owner) / `ADMIN` | `PrintJobControllerTest`, `PrintQueueE2ETest#E2E-05` |
| **8** | `GET` | `/api/jobs/{id}/history` | Path: `id` | `List<PrintJobHistoryResponse>` | `200 OK` | Bearer JWT | `USER` (Owner) / `ADMIN` | `PrintQueueE2ETest#E2E-05` |
| **9** | `DELETE` | `/api/jobs/{id}` | Path: `id` | Empty | `204 No Content` | Bearer JWT | `USER` (Owner) / `ADMIN` | `PrintJobControllerTest`, `PrintQueueE2ETest#E2E-06` |
| **10** | `GET` | `/api/queue` | None | `List<QueueItemResponse>` | `200 OK` | None (Public) | Anyone | `QueueControllerTest`, `PrintQueueE2ETest#E2E-04` |
| **11** | `GET` | `/api/queue/status` | None | `QueueStatusResponse` | `200 OK` | None (Public) | Anyone | `QueueControllerTest`, `PrintQueueE2ETest#E2E-04` |
| **12** | `GET` | `/api/printer/status` | None | `PrinterResponse` | `200 OK` | None (Public) | Anyone | `PrinterControllerTest`, `PrintQueueE2ETest#E2E-09` |
| **13** | `POST` | `/api/printer/start` | None | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `PrinterControllerTest`, `PrintQueueE2ETest#E2E-10` |
| **14** | `POST` | `/api/printer/pause` | None | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `PrinterControllerTest`, `PrinterStateConsistencyTest` |
| **15** | `POST` | `/api/printer/resume` | None | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `PrinterControllerTest`, `PrinterStateConsistencyTest` |
| **16** | `POST` | `/api/printer/reset` | None | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `PrinterControllerTest`, `PrintQueueE2ETest#E2E-09` |
| **17** | `POST` | `/api/printer/error` | None | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `PrinterControllerTest`, `PrintQueueE2ETest#E2E-09` |
| **18** | `GET` | `/api/admin/users` | None | `List<UserResponse>` | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest`, `PrintQueueE2ETest#E2E-11` |
| **19** | `GET` | `/api/admin/jobs` | None | `List<PrintJobResponse>` | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest`, `PrintQueueE2ETest#E2E-08` |
| **20** | `GET` | `/api/admin/statistics` | None | `AdminStatisticsResponse` | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest`, `PrintQueueE2ETest#E2E-08` |
| **21** | `PUT` | `/api/admin/jobs/{id}/priority` | `PriorityUpdateRequest` | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest`, `PrintQueueE2ETest#E2E-08` |
| **22** | `PUT` | `/api/admin/jobs/{id}/cancel` | Path: `id` | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest`, `PrintQueueE2ETest#E2E-08` |
| **23** | `PUT` | `/api/admin/jobs/{id}/retry` | Path: `id` | Empty | `200 OK` | Bearer JWT | `ROLE_ADMIN` | `AdminControllerTest` |
| **24** | `GET` | `/actuator/health` | None | `{"status":"UP",...}` | `200 OK` | None (Public) | Anyone | `PrintQueueApplicationTest` |

---

## 2. Frontend to Backend Contract Alignment

All 13 HTML pages and `app.js` invoke exact REST contracts without discrepancies:
- `app.js` `auth.login()` ➔ calls `POST /api/auth/login`.
- `app.js` `auth.register()` ➔ calls `POST /api/auth/register`.
- `submit-job.html` ➔ sends `multipart/form-data` with `file` and `jobDetails` JSON to `POST /api/jobs`.
- `my-jobs.html` ➔ calls `GET /api/jobs` and `DELETE /api/jobs/{id}`.
- `job-details.html` ➔ calls `GET /api/jobs/{id}` and `GET /api/jobs/{id}/history`.
- `queue.html` ➔ calls `GET /api/queue` and `GET /api/queue/status`.
- `admin-dashboard.html` ➔ calls `GET /api/admin/statistics`.
- `admin-queue.html` ➔ calls `GET /api/admin/jobs`, `PUT /api/admin/jobs/{id}/priority`, and `PUT /api/admin/jobs/{id}/cancel`.
- `users.html` ➔ calls `GET /api/admin/users`.
- `printer.html` ➔ calls `GET /api/printer/status`, `POST /api/printer/start`, `/pause`, `/resume`, `/reset`, and `/error`.
