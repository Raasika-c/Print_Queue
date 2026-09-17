# Digital Printing Queue Management System

> **Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027

A real, working, full-stack web application that manages a digital print job queue with virtual printer simulation, JWT authentication, file upload, and a complete CI/CD DevOps pipeline.

---

## 🖨️ Features

- **JWT Authentication & Security** — Self-registration, BCrypt password hashing, role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
- **Print Job Management** — Multipart document uploads (PDF, DOCX, PNG, JPG), automatic page calculation, dynamic cost estimation (₹5/Color, ₹1/B&W).
- **Dynamic Priority & FIFO Queue** — Prioritized scheduling (`HIGH`, `NORMAL`, `LOW`) with FIFO ordering, automatic queue slot recalculation, real-time live positions.
- **Virtual Hardware Simulation** — Non-blocking background printer (`PRINTER-01`) with realistic page-by-page simulation, progress percentage, and deterministic FSM states (`IDLE`, `PRINTING`, `PAUSED`, `ERROR`, `OFFLINE`).
- **Admin Management & Audit Trail** — Centralized administration console, 10-metric real-time statistics dashboard, priority override, job cancellation/retry, and `PrintJobHistory` lifecycle auditing.
- **13 Complete Frontend Pages** — Responsive Bootstrap 5 UI connected to real backend endpoints (`index.html`, `login.html`, `register.html`, `dashboard.html`, `submit-job.html`, `my-jobs.html`, `job-details.html`, `queue.html`, `admin-dashboard.html`, `admin-queue.html`, `users.html`, `printer.html`, `error.html`).
- **Comprehensive Quality Assurance** — 120 automated tests across a 5-tier test pyramid (100% pass rate, zero failures).
- **Complete Integrated DevOps Pipeline** — Git ➔ GitHub Webhook ➔ 11-Stage Jenkins Pipeline ➔ Quality Gate ➔ Docker Multi-stage ➔ Ansible Automation ➔ Actuator Health Check.

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Language | Java | 21 (LTS) |
| Framework | Spring Boot | 3.3.4 |
| Security | Spring Security + JJWT | 6.x / 0.12.6 |
| Persistence | Spring Data JPA + Hibernate | 6.x |
| Database | MySQL + Flyway Migrations | 8.0 / 10.x |
| In-Memory DB | H2 (MySQL Mode) for Testing | 2.x |
| Build Tool | Apache Maven | 3.9.16 |
| Testing | JUnit 5, Mockito, MockMvc, AssertJ | 5.x |
| Frontend | HTML5, CSS3, Bootstrap 5, Vanilla JS | 5.3 |
| Containers | Docker + Docker Compose | 29.x / 2.x |
| CI/CD Engine | Jenkins Declarative Pipeline | 2.x |
| Configuration | Ansible | 2.x |
| VCS | Git + GitHub Webhooks | — |

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- Java 21+
- Maven 3.9+
- MySQL 8.0 running locally

### 1. Clone the repository
```bash
git clone https://github.com/your-org/digital-print-queue.git
cd digital-print-queue
```

### 2. Create the database
```sql
CREATE DATABASE printqueue_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'printqueue_user'@'localhost' IDENTIFIED BY 'printqueue_pass';
GRANT ALL PRIVILEGES ON printqueue_db.* TO 'printqueue_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure environment (optional — defaults work for local dev)
```bash
copy .env.example .env
# Edit .env with your database credentials
```

### 4. Build and run
```bash
mvn clean package -DskipTests
java -jar target/digital-print-queue-1.0.0.jar
```

### 5. Access the application
- **App**: http://localhost:8080
- **Health**: http://localhost:8080/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui.html

---

## 🔑 Demo Credentials

> ⚠️ **DEMO ONLY — Academic use. Change before any production deployment.**

| Role | Email | Password |
|------|-------|----------|
| **ADMIN** | `admin@digitalprint.com` | `Admin@123` |

---

## 🧪 Running Tests & Regression Suite

```bash
# Run complete 118-test regression suite
mvn test

# Run specific test tier
mvn test -Dtest=PrintQueueE2ETest
mvn test -Dtest=DatabaseConsistencyTest,PrinterStateConsistencyTest,QueueConsistencyTest

# Generate Surefire HTML test report
mvn surefire-report:report
# Report output: target/site/surefire-report.html
```

---

## 🐳 Docker Containerization

```bash
# Build production multi-stage image
docker build -t digital-print-queue:latest .

# Run application and MySQL with persistent storage
docker-compose up -d

# Verify container status and health
docker-compose ps
curl http://localhost:8080/actuator/health

# Stop containers (preserves upload volume)
docker-compose down
```

---

## 📁 Project Structure

```
digital-print-queue/
├── src/
│   ├── main/
│   │   ├── java/com/printqueue/
│   │   │   ├── PrintQueueApplication.java
│   │   │   ├── config/          # SecurityConfig, DataInitializer, AsyncConfig
│   │   │   ├── controller/      # Auth, PrintJob, Queue, Printer, Admin Controllers
│   │   │   ├── dto/             # Request & Response DTOs
│   │   │   ├── entity/          # User, PrintJob, VirtualPrinter, PrintJobHistory
│   │   │   ├── exception/       # GlobalExceptionHandler, Custom Exceptions
│   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   ├── security/        # JWT Token Provider & Filter, CustomUserDetailsService
│   │   │   └── service/         # Business Services & Printer Simulation Engine
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-docker.properties
│   │       ├── db/migration/    # Flyway V1..V5 Migration Scripts
│   │       └── static/          # 13 Complete Frontend HTML Pages, CSS, & JS
│   └── test/java/com/printqueue/ # 118 Automated Tests (Levels 1 to 5)
├── ansible/                     # Ansible Playbooks, Inventory, and Nginx Template
├── Jenkinsfile                  # 11-Stage Automated CI/CD Pipeline
├── Dockerfile                   # Multi-Stage Production Container Build
├── Dockerfile.alpine            # Alpine OS Comparison Image
├── Dockerfile.ubuntu            # Ubuntu OS Comparison Image
├── docker-compose.yml           # Multi-Container Compose Orchestration
└── pom.xml
```

---

## 📋 API Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register new user account |
| `POST` | `/api/auth/login` | Public | Login and obtain JWT token |
| `GET` | `/api/auth/me` | USER / ADMIN | Current authenticated user profile |
| `POST` | `/api/auth/logout` | Any | Discard JWT token |
| `POST` | `/api/jobs` | USER | Submit print job (multipart file + options) |
| `GET` | `/api/jobs` | USER | View user's submitted print jobs |
| `GET` | `/api/jobs/{id}` | USER | View job details and real-time status |
| `DELETE`| `/api/jobs/{id}` | USER | Cancel submitted/queued print job |
| `GET` | `/api/jobs/{id}/history` | USER | View audit lifecycle history of a job |
| `GET` | `/api/queue` | Public | View real-time active print queue |
| `GET` | `/api/queue/status` | Public | View queue length, paused state, and active jobs |
| `GET` | `/api/printer/status`| Public | View PRINTER-01 state, progress, and current job |
| `POST` | `/api/printer/start` | ADMIN | Start printer job processing |
| `POST` | `/api/printer/pause` | ADMIN | Pause printer during active printing |
| `POST` | `/api/printer/resume`| ADMIN | Resume printer from paused state |
| `POST` | `/api/printer/reset` | ADMIN | Reset printer from ERROR/OFFLINE to IDLE |
| `POST` | `/api/printer/error` | ADMIN | Inject hardware simulation error |
| `GET` | `/api/admin/users` | ADMIN | List all registered user accounts |
| `GET` | `/api/admin/jobs` | ADMIN | View all system print jobs with filters |
| `GET` | `/api/admin/statistics`| ADMIN | Live dashboard statistics across 10 metrics |
| `PUT` | `/api/admin/jobs/{id}/priority` | ADMIN | Override job priority (HIGH / NORMAL / LOW) |
| `PUT` | `/api/admin/jobs/{id}/cancel` | ADMIN | Administratively cancel any print job |
| `PUT` | `/api/admin/jobs/{id}/retry` | ADMIN | Re-queue a failed print job |
| `GET` | `/actuator/health` | Public | Spring Boot Actuator health status check |

---

## 🔄 Integrated CI/CD DevOps Pipeline

```
DEVELOPER ──► GIT ──► GITHUB ──► WEBHOOK ──► JENKINS
                                                │
 ┌──────────────────────────────────────────────┴───────────────────────────────────────────┐
 │ 1. Checkout  ──► 2. Build  ──► 3. Unit Tests  ──► 4. Integration Tests  ──► 5. API Tests │
 │                                                                                          │
 │ 6. E2E Tests ──► 7. QUALITY GATE (Surefire XML Report & 100% Pass Enforced)              │
 │                                                                                          │
 │ 8. Package (JAR) ──► 9. Docker Build ──► 10. Ansible Deploy ──► 11. Health Check (UP)   │
 └──────────────────────────────────────────────┬───────────────────────────────────────────┘
                                                ▼
                                    DEPLOYED APPLICATION
                                  (http://localhost:8080)
```

> **Strict Quality Gate Rule**: If any compilation error or any of the 120 automated tests fail, the pipeline immediately halts. Packaging, Docker image generation, and Ansible container deployment are strictly blocked.

---

## 📄 Complete Project Documentation

| Document | Purpose |
|----------|---------|
| [PROJECT_CONTRACT.md](PROJECT_CONTRACT.md) | Single Source of Truth & Architecture Invariants |
| [FEATURE_REGISTRY.md](FEATURE_REGISTRY.md) | Feature traceability matrix (F-001 through F-027) |
| [ARCHITECTURE.md](ARCHITECTURE.md) | System design, component diagrams, & database schema |
| [DEVOPS_PIPELINE.md](DEVOPS_PIPELINE.md) | Complete End-to-End DevOps Pipeline & Quality Gate Guide |
| [FINAL_TEST_REPORT.md](FINAL_TEST_REPORT.md) | Final QA Audit & 120-Test Regression Report |
| [EXPERIMENT_MAPPING.md](EXPERIMENT_MAPPING.md) | 23IT723 Laboratory Syllabus to Codebase Mapping |
| [DEMO_SCRIPT.md](DEMO_SCRIPT.md) | 10–15 Minute Examiner Demonstration Script |
| [VIVA_QUESTIONS.md](VIVA_QUESTIONS.md) | 105 Technical Viva Questions & Detailed Answers |
| [QA_REPORT.md](QA_REPORT.md) | Automated QA & Test Pyramid Report |
| [TESTING.md](TESTING.md) | Testing pyramid hierarchy & command reference |
| [DOCKER.md](DOCKER.md) | Docker containerization, volume persistence, & OS comparison |
| [ANSIBLE.md](ANSIBLE.md) | Ansible automation, idempotency, & playbook execution |
| [JENKINS.md](JENKINS.md) | Jenkins installation, pipeline setup, & credentials |
| [GITHUB_WEBHOOK.md](GITHUB_WEBHOOK.md) | GitHub Webhook integration & student-lab tunneling |
| [GIT_WORKFLOW.md](GIT_WORKFLOW.md) | Git branching strategy, operations, & conflict resolution |
| [CHANGELOG.md](CHANGELOG.md) | Chronological version release history |

---

## 👨‍🏫 Academic Information

**Course**: 23IT723 – DevOps Laboratory  
**Academic Year**: 2026–2027  
**Project**: Digital Printing Queue Management System  
**DevOps Tools**: Git, GitHub, Jenkins, Docker, Ansible
