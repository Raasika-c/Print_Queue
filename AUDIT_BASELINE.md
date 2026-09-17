# AUDIT BASELINE REPORT
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Audit Date**: 2026-09-17 | **Branch**: `validation/full-project-audit`

---

## 1. System Environment Baseline

| Parameter | Detected Value | Verification Method | Status |
|:----------|:---------------|:--------------------|:------:|
| **Project Absolute Path** | `c:\Users\ELCOT\Documents\DEV-RAASIKA` | System Environment | 🟢 Verified |
| **Operating System** | Windows 11 (10.0), arch `amd64` | `java.lang.System` / OS Host | 🟢 Verified |
| **Java Development Kit** | Java SE 21.0.11 LTS (build 21.0.11+9-LTS-211) | `java -version` | 🟢 Verified |
| **Java Vendor & Home** | Oracle Corporation, `C:\Program Files\Java\jdk-21.0.11` | `mvn -v` | 🟢 Verified |
| **Build Automation Tool** | Apache Maven 3.9.16 | `mvn -v` | 🟢 Verified |
| **Maven Home** | `C:\apache-maven-3.9.16` | System Environment | 🟢 Verified |
| **Framework & Version** | Spring Boot 3.3.4 | `pom.xml` dependency | 🟢 Verified |
| **Version Control Tool** | Git 2.54.0.windows.1 | `git --version` | 🟢 Verified |
| **Docker CLI** | Docker version 29.7.2, build `a7dcaa6` | `docker --version` | 🟢 Verified |
| **Docker Compose** | Docker Compose version v5.4.0 | `docker compose version` | 🟢 Verified |
| **Docker Daemon Status** | Desktop Engine Linux pipe offline | `docker ps` | ⚠️ Host daemon offline |
| **Ansible Runtime** | Not in Windows native PATH (Linux WSL2 target) | `where ansible` | ⚠️ Target Node Lab |
| **Jenkins Daemon** | Declarative pipeline defined (`Jenkinsfile`) | Inspection | ⚠️ Standalone Service Lab |

---

## 2. Project Component Inventory

### 2.1 Backend Architecture
- **Package Base**: `com.printqueue`
- **Main Class**: `PrintQueueApplication.java` (`@SpringBootApplication`, `@EnableScheduling`)
- **Controllers (5)**: `AuthController`, `PrintJobController`, `QueueController`, `PrinterController`, `AdminController`
- **Services (6)**: `UserService`, `PrintJobService`, `QueueService`, `VirtualPrinterService`, `PrintingSimulationService`, `AdminService`, `PrintJobHistoryService`, `FileStorageService`
- **Repositories (5)**: `UserRepository`, `PrintJobRepository`, `PrinterRepository`, `PrintJobHistoryRepository`, `AuditLogRepository`
- **Entities (4)**: `User`, `PrintJob`, `VirtualPrinter`, `PrintJobHistory`
- **Security**: Stateless JWT (`JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`, `SecurityConfig`, `UserDetailsServiceImpl`)
- **DTOs**: Request DTOs (`RegisterRequest`, `LoginRequest`, `PrintJobRequest`, `PriorityUpdateRequest`), Response DTOs (`AuthResponse`, `UserResponse`, `PrintJobResponse`, `PrinterResponse`, `QueueItemResponse`, `QueueStatusResponse`, `AdminStatisticsResponse`, `ApiError`)

### 2.2 Database Configuration & Flyway Migrations
- **Default Database**: MySQL 8.0 (`jdbc:mysql://localhost:3306/printqueue_db`)
- **Connection Pool**: HikariCP (max-pool-size: 10, min-idle: 2, timeout: 30000ms)
- **Schema Management**: Flyway migration scripts in `src/main/resources/db/migration/`:
  - `V1__Initial_Schema.sql`: Creates `users`, `print_jobs`, `printers`, `audit_logs`, `app_settings`
  - `V2__Seed_Data.sql`: Initial seed data for printers and app settings
  - `V3__Virtual_Printer_PRINTER01.sql`: Printer hardware configuration
  - `V4__Admin_Management_And_History.sql`: Creates `print_job_history` table
  - `V5__Extend_Users_Table.sql`: User profile fields
- **Test Database**: In-memory H2 in MySQL compatibility mode (`jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1`)

### 2.3 User Interface (Frontend Assets)
- **Directory**: `src/main/resources/static/`
- **13 Complete Pages**:
  1. `index.html` (Landing & live queue view)
  2. `login.html` (Authentication portal)
  3. `register.html` (User registration)
  4. `dashboard.html` (User dashboard)
  5. `submit-job.html` (Multipart document submission)
  6. `my-jobs.html` (Personal job history)
  7. `job-details.html` (Detailed job inspector & progress)
  8. `queue.html` (Live queue display)
  9. `admin-dashboard.html` (10-metric admin console)
  10. `admin-queue.html` (Priority & job management)
  11. `users.html` (System accounts directory)
  12. `printer.html` (Hardware simulator console)
  13. `error.html` (Error boundary page)
- **Supporting Assets**: `css/styles.css`, `js/app.js`

### 2.4 DevOps Assets
- **CI/CD Pipeline**: `Jenkinsfile` (11 stages: Checkout, Build, Unit Tests, Integration Tests, API Tests, E2E & Smoke Tests, Quality Gate, Package, Docker Build, Ansible Deployment, Health Check)
- **Containerization**:
  - `Dockerfile`: Multi-stage build with non-root security (`appuser:10001`)
  - `Dockerfile.alpine`: Alpine Linux base image variant
  - `Dockerfile.ubuntu`: Ubuntu Jammy LTS base image variant
  - `docker-compose.yml`: Application and MySQL services, bridge network, persistent volume `printqueue_uploads`
  - `.dockerignore`: Exclusion rules for build artifacts and temporary files
- **Configuration Management**:
  - `ansible/inventory.ini`: Node classification
  - `ansible/site.yml`: Master orchestration playbook
  - `ansible/docker.yml`: Docker engine installation
  - `ansible/webserver.yml`: Nginx reverse proxy configuration
  - `ansible/deploy.yml`: Container and volume deployment
  - `ansible/templates/nginx.conf.j2`: Reverse proxy template
