# PRACTICAL DEMO SCRIPT (10–15 MINUTES)
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Audience**: Internal / External Practical Examiners & Evaluators

---

## Demonstration Timeline Overview (15 Minutes)

```
00:00 - 02:00 ──► Part 1: System Overview & Architecture (2 mins)
02:00 - 05:00 ──► Part 2: Application Live User & Admin Workflows (3 mins)
05:00 - 07:00 ──► Part 3: Git Version Control & Branching Strategy (2 mins)
07:00 - 09:00 ──► Part 4: Automated Testing & Test Pyramid (2 mins)
09:00 - 11:00 ──► Part 5: Docker Containerization & Volume Persistence (2 mins)
11:00 - 13:00 ──► Part 6: Ansible Automation & Idempotency (2 mins)
13:00 - 15:00 ──► Part 7: Integrated CI/CD Pipeline & Quality Gate (2 mins)
```

---

## Part 1: System Overview & Architecture (2 Minutes)

### Presenter Speaking Script:
> *"Good morning, esteemed examiners. Today I am presenting the **Digital Printing Queue Management System**, built for the 23IT723 DevOps Laboratory course.
> This is a complete, production-grade application running on Java 21, Spring Boot 3.3.4, and MySQL 8.0, featuring a responsive Bootstrap 5 frontend across 13 dedicated pages.
> Beyond core application features—such as multipart file uploads, priority queue scheduling, and realistic virtual printer simulation—the central achievement of this project is the end-to-end DevOps automation lifecycle integrating Git, GitHub Webhooks, Jenkins, Docker, Ansible, and automated Quality Gates."*

### Visual Asset to Show:
- Open browser to landing page: `http://localhost:8080/index.html`.
- Highlight live stats banner and navigation links.

---

## Part 2: Application Live User & Admin Workflows (3 Minutes)

### Step 2.1: Student User Workflow
1. **Navigate to Registration**: Click **Register** (`register.html`).
   - Register a student account: Name: `Rahul Sharma`, Email: `rahul@student.edu`, Password: `Password@123`.
   - Explain password complexity enforcement (BR-002: uppercase, lowercase, digit, special character).
2. **User Login**: Sign in on `login.html`.
   - Explain stateless JWT authentication and 24-hour expiration token stored in `localStorage`.
3. **Submit a Print Job**: Click **Submit Print Job** (`submit-job.html`).
   - Attach sample PDF file: `assignment.pdf`.
   - Select: `Color: COLOR`, `Copies: 2`, `Priority: NORMAL`.
   - Point out the real-time dynamic cost calculation preview:
     $$\text{Cost} = 2 \text{ copies} \times 1 \text{ page} \times \text{₹}5.00 = \text{₹}10.00$$
   - Click **Submit Print Job**.
   - Show returned Job Number: `DPQ-2026-0004` and Assigned Queue Position `#1`.
4. **Inspect Live Queue**: Open `queue.html`.
   - Point out the job at position `#1` with status `SUBMITTED`.

### Step 2.2: Administrator Workflow & Hardware Simulation
1. **Admin Sign-In**: Open private window or sign out and log in as:
   - Email: `admin@digitalprint.com` | Password: `Admin@123`.
2. **Admin Dashboard (`admin-dashboard.html`)**:
   - Point out live statistics across all 10 metrics (Total Users, Total Jobs, Current Queue Length, etc.).
3. **Hardware Simulation Console (`printer.html`)**:
   - Show `PRINTER-01` in `IDLE` state.
   - Click **Start Printer**.
   - Show printer status transition to `PRINTING`, acquiring Job `DPQ-2026-0004`.
   - Watch live progress bar advance page-by-page ($0\% \rightarrow 50\% \rightarrow 100\%$) driven by the background simulation thread.
   - Show final transition to `COMPLETED` and printer returning to `IDLE`.
4. **Audit Log Inspection**: Open `job-details.html?id=4`.
   - Show chronological audit trail recorded in `print_job_history`:
     `QUEUED ➔ PRINTING ➔ COMPLETED` with timestamps and user attribution.

---

## Part 3: Git Version Control & Branching Strategy (2 Minutes)

### Presenter Speaking Script:
> *"Now let us examine Experiment 1 & 2: Version Control using Git. We follow the enterprise GitFlow model with 9 structured branches."*

### Terminal Commands to Run:
```bash
# 1. Show branch structure
git branch -a

# 2. Show recent commit history and clean linear graph
git log --oneline --graph --decorate -n 6

# 3. Show working tree cleanliness
git status
```

### Explaining Key Highlights:
- Point out dedicated branches: `main` (production), `develop` (staging), and the 7 feature branches (`feature/authentication`, `feature/print-job`, `feature/queue`, etc.).
- Explain the safe merge conflict demonstration documented in `GIT_WORKFLOW.md`.

---

## Part 4: Automated Testing & Test Pyramid (2 Minutes)

### Presenter Speaking Script:
> *"In Experiment 4, we implemented a comprehensive 5-tier test pyramid. Every single business rule, database constraint, and user workflow is continuously validated across 120 automated tests."*

### Terminal Commands to Run:
```bash
# Execute complete regression test suite
mvn test -Dtest=RegressionTestSuite
```

### Explaining Test Pyramid Tiers:
- **Level 1 (Unit)**: 57 tests verifying password hashing, token validation, and cost algorithms in isolation.
- **Level 2 (Repository)**: 18 tests verifying JPA queries, foreign key constraints, and unique email constraints with in-memory H2 MySQL mode.
- **Level 3 (FSM & Consistency)**: 5 tests verifying deterministic state machine transitions and queue slot recalculation.
- **Level 4 (API / MockMvc)**: 28 tests verifying HTTP status codes, validation errors, and role authorization (`ROLE_ADMIN` vs `ROLE_USER`).
- **Level 5 (E2E & Frontend)**: 25 tests verifying all 13 HTML pages (`FrontendPagesIntegrationTest`) and complete multi-step user/admin journeys (`PrintQueueE2ETest`).

### Show Test Outcome:
```
[INFO] Results:
[INFO] Tests run: 120, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Part 5: Docker Containerization & Volume Persistence (2 Minutes)

### Presenter Speaking Script:
> *"For Experiments 7, 8, and 9, the entire stack is containerized using multi-stage Docker builds and Docker Compose."*

### Key Files to Show:
- **`Dockerfile`**:
  - Stage 1: Maven 3.9 + Temurin 21 Jammy compiles the source code.
  - Stage 2: Minimal Temurin 21 JRE Jammy runtime.
  - Security: Non-root user `appuser` (UID 10001) for container isolation.
- **`Dockerfile.alpine` vs `Dockerfile.ubuntu`**:
  - Show the OS comparison experiment documented in `DOCKER.md`:
    - Alpine: **185 MB** (48.6% size reduction, minimal CVE footprint).
    - Ubuntu: **360 MB** (glibc enterprise compatibility).
- **`docker-compose.yml`**:
  - Dual microservices: `app` (port 8080) and `mysql:8.0` (port 3306).
  - Health check dependency: `service_healthy`.
  - Named persistent volume: `printqueue_uploads` mapped to `/app/uploads`.

### Volume Persistence Explanation:
> *"Even if the application container is deleted with `docker-compose down`, all uploaded documents and generated PDFs remain completely safe and intact in the persistent storage volume `printqueue_uploads`."*

---

## Part 6: Ansible Automation & Idempotency (2 Minutes)

### Presenter Speaking Script:
> *"In Experiment 10, we automated server configuration and deployment using Ansible. Our playbooks are 100% idempotent."*

### Key Files to Show:
- `ansible/inventory.ini`: Categorizes target nodes into `appservers`, `webservers`, and `dbservers`.
- `ansible/site.yml`: Master orchestration playbook importing:
  - `docker.yml`: Idempotent installation of Docker runtime.
  - `webserver.yml`: Nginx reverse proxy configuration.
  - `deploy.yml`: Volume provisioning, environment loading, container startup, and `/actuator/health` verification.
- `ansible/templates/nginx.conf.j2`: Reverse proxy configuration routing external port 80 to Spring Boot port 8080.

### Idempotency Demonstration:
```bash
# First Run: Provisions environment and deploys container
ansible-playbook -i ansible/inventory.ini ansible/site.yml
# Recap: ok=9, changed=4

# Second Run (Idempotency Proof):
ansible-playbook -i ansible/inventory.ini ansible/site.yml
# Recap: ok=9, changed=0 (No unnecessary changes made!)
```

---

## Part 7: Integrated CI/CD Pipeline & Quality Gate (2 Minutes)

### Presenter Speaking Script:
> *"Finally, in Experiment 11, everything converges into an integrated CI/CD pipeline governed by a strict Quality Gate."*

### Step 7.1: The Happy Path Demonstration
1. Developer pushes code to GitHub: `git push origin develop`.
2. GitHub Webhook triggers Jenkins via `/github-webhook/`.
3. Jenkins executes all 11 stages in sequence:
   $$\text{Checkout} \rightarrow \text{Build} \rightarrow \text{Unit Tests} \rightarrow \text{Integration Tests} \rightarrow \text{API Tests} \rightarrow \text{E2E Tests} \rightarrow \text{Quality Gate} \rightarrow \text{Package} \rightarrow \text{Docker Build} \rightarrow \text{Ansible Deploy} \rightarrow \text{Health Check}$$
4. Automated Quality Gate verifies 120/120 tests pass.
5. Deployed application health is confirmed via `/actuator/health` returning `{"status":"UP"}`.

### Step 7.2: The Failure Scenario Demonstration (Deployment Guard)
> *"What happens if a developer pushes broken code? Let me explain the automated deployment guard."*
- If an assertion fails or a compilation error occurs:
  1. Surefire catches the failure and exits with code `1`.
  2. Jenkins immediately flags the build as **FAILED (Red)**.
  3. **Quality Gate, Packaging, Docker Build, and Ansible Deployment are completely BLOCKED**.
  4. The production environment remains safe and untouched.

---

## Conclusion & Q&A Transition
> *"In summary, the Digital Printing Queue Management System satisfies all curriculum requirements of 23IT723 DevOps Laboratory, showcasing practical software engineering, automated testing, containerization, configuration management, and continuous delivery.
> Thank you, and I welcome your questions."*
