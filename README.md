# Digital Printing Queue Management System

> **Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027

A real, working, full-stack web application that manages a digital print job queue with virtual printer simulation, JWT authentication, file upload, and a complete CI/CD DevOps pipeline.

---

## 🖨️ Features

- **User Registration & Login** — JWT-secured REST API, BCrypt password hashing
- **Print Job Submission** — File upload (PDF/images, max 10MB), cost calculation
- **FIFO Queue** — Jobs processed in submission order across 3 virtual printers
- **Virtual Printers** — 3 simulated printers (A, B, C) with IDLE/BUSY/OFFLINE states
- **Admin Dashboard** — Manage jobs, printers, queue, and view audit logs
- **Cost Calculation** — COLOR: ₹5/page × copies | B&W: ₹1/page × copies
- **Audit Logging** — Complete trail of all state-changing actions
- **Full DevOps Pipeline** — Git → GitHub → Jenkins → Docker → Ansible

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Migration | Flyway |
| Build | Maven 3.9 |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Frontend | HTML + CSS + Vanilla JS |
| Container | Docker + Docker Compose |
| CI/CD | Jenkins (Declarative Pipeline) |
| Config Mgmt | Ansible |

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

## 🧪 Running Tests

```bash
# Run all unit tests
mvn test

# Run with test report
mvn test surefire-report:report

# Compile only
mvn clean compile

# Full build (skip tests)
mvn clean package -DskipTests
```

---

## 🐳 Docker

```bash
# Build image
docker build -t digital-print-queue:1.0.0 .

# Run with Docker Compose (app + MySQL)
docker-compose up -d

# Check health
curl http://localhost:8080/actuator/health

# Stop
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
│   │   │   ├── config/          # SecurityConfig, DataInitializer
│   │   │   ├── controller/      # AuthController, ...
│   │   │   ├── dto/             # request/, response/
│   │   │   ├── entity/          # User, PrintJob, Printer, ...
│   │   │   ├── exception/       # GlobalExceptionHandler, custom exceptions
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   ├── security/        # JWT, UserDetailsService
│   │   │   └── service/         # UserService, ...
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-docker.properties
│   │       └── db/migration/    # Flyway SQL scripts
│   └── test/
│       └── java/com/printqueue/ # JUnit 5 tests
├── ansible/                     # Deployment automation
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── .env.example
├── PROJECT_CONTRACT.md
├── FEATURE_REGISTRY.md
├── ARCHITECTURE.md
├── TESTING.md
└── CHANGELOG.md
```

---

## 📋 API Overview

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login, get JWT |
| GET | `/api/auth/me` | USER | Current user profile |
| POST | `/api/auth/logout` | Any | Logout |
| POST | `/api/jobs` | USER | Submit print job |
| GET | `/api/jobs` | USER | View own jobs |
| GET | `/api/jobs/{id}` | USER | Job details |
| DELETE | `/api/jobs/{id}` | USER | Cancel job |
| GET | `/api/admin/jobs` | ADMIN | All jobs |
| GET | `/api/admin/printers` | ADMIN | All printers |
| GET | `/api/admin/stats` | ADMIN | Statistics |
| GET | `/actuator/health` | Public | Health check |

---

## 🔄 CI/CD Pipeline

```
Developer → Git Push → GitHub → Jenkins
  → Checkout → Build → Unit Tests → Integration Tests
  → Docker Build → Docker Compose Up → Smoke Test
  → Ansible Deploy → Health Check
```

---

## 📄 Documentation

| Document | Purpose |
|----------|---------|
| [PROJECT_CONTRACT.md](PROJECT_CONTRACT.md) | Single source of truth |
| [FEATURE_REGISTRY.md](FEATURE_REGISTRY.md) | Feature tracking |
| [ARCHITECTURE.md](ARCHITECTURE.md) | System design & diagrams |
| [TESTING.md](TESTING.md) | Testing strategy & commands |
| [CHANGELOG.md](CHANGELOG.md) | Change history |

---

## 👨‍🏫 Academic Information

**Course**: 23IT723 – DevOps Laboratory  
**Academic Year**: 2026–2027  
**Project**: Digital Printing Queue Management System  
**DevOps Tools**: Git, GitHub, Jenkins, Docker, Ansible  
