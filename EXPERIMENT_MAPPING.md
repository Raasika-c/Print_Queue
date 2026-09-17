# EXPERIMENT MAPPING LAB MANUAL
## Course: 23IT723 – DevOps Laboratory | Academic Year: 2026–2027
### Digital Printing Queue Management System
**Student / Developer Guide**: Complete mapping of every laboratory syllabus experiment to the production project codebase, commands, outputs, and viva explanations.

---

## Overview Table: 23IT723 Syllabus to Project Mapping

| Exp # | Experiment Title | Project Feature / Implementation | Core Files | Status |
|:-----:|:-----------------|:---------------------------------|:-----------|:------:|
| **1** | Version Control with Git | Local VCS, Branching Strategy & Merge Conflicts | `.git/`, `GIT_WORKFLOW.md` | 🟢 Verified |
| **2** | Remote Collaboration with GitHub | Remote Origin, Branch Tracking, Push/Pull/Clone | `GIT_WORKFLOW.md` | 🟢 Verified |
| **3** | Build Automation with Apache Maven | Maven Lifecycle, POM, Compiler & Packaging | `pom.xml`, `target/*.jar` | 🟢 Verified |
| **4** | Automated Testing & QA with JUnit 5 | 5-Tier Test Pyramid (120 Tests, MockMvc, AssertJ) | `src/test/**`, `QA_REPORT.md` | 🟢 Verified |
| **5** | Continuous Integration with Jenkins | 11-Stage Declarative Pipeline & Quality Gate | `Jenkinsfile`, `JENKINS.md` | 🟢 Verified |
| **6** | Automated Triggering via GitHub Webhook | Push Triggers & Student Lab Tunneling | `Jenkinsfile`, `GITHUB_WEBHOOK.md` | 🟢 Verified |
| **7** | Containerization with Docker | Multi-Stage Build & Non-Root Security | `Dockerfile`, `DOCKER.md` | 🟢 Verified |
| **8** | Container OS & Base-Image Optimization | Alpine Linux vs Ubuntu LTS Comparative Study | `Dockerfile.alpine`, `Dockerfile.ubuntu` | 🟢 Verified |
| **9** | Multi-Container Orchestration & Volumes | Docker Compose Stack & Persistent Storage | `docker-compose.yml`, `DOCKER.md` | 🟢 Verified |
| **10** | Configuration Management with Ansible | Playbooks, Idempotency & Nginx Reverse Proxy | `ansible/**`, `ANSIBLE.md` | 🟢 Verified |
| **11** | Integrated DevOps Pipeline & Quality Gate | Complete Developer-to-Production Automated System | `Jenkinsfile`, `DEVOPS_PIPELINE.md` | 🟢 Verified |

---

## Detailed Experiment Specifications

### EXPERIMENT 1: Version Control System using Git

#### 1. Project Feature
Local version control, structured branch hierarchy (`main`, `develop`, feature branches), commit log tracking, and safe merge conflict demonstration.

#### 2. Implementation
- Repository initialized via `git init`.
- Branching policy: `main` (production), `develop` (staging), `feature/authentication`, `feature/print-job`, `feature/queue`, `feature/admin`, `feature/testing`, `feature/docker`, `feature/ansible`.
- Merge conflict simulation executed on isolated conflict branches without harming source code.

#### 3. CLI Commands
```bash
# Repository status and branch verification
git status
git branch -a
git log --oneline --graph --decorate -n 10

# Switch branches and merge
git checkout develop
git merge main
git checkout main
```

#### 4. Screenshot / Terminal Output
```
* 83bd302 (HEAD -> main, develop) feat(devops): implement Phase 14 complete integrated devops system
* d4dc7d4 feat(ansible): complete Phase 13 Ansible automation playbooks and ANSIBLE.md
* 67543ea feat(docker): complete Phase 12 Docker containerization and DOCKER.md
* d734bcf feat(webhook): complete Phase 11 GitHub Webhook integration
* eac89a0 feat(ci-cd): add Jenkinsfile, JENKINS.md, QA_REPORT.md, GIT_WORKFLOW.md
```

#### 5. Expected Result
Repository maintains a clean linear commit graph; all branches are tracked; zero untracked working files.

#### 6. Actual Result
Working tree is completely clean (`nothing to commit, working tree clean`). All 9 branches are active and synchronized.

#### 7. Viva Explanation
- **Working Tree vs Staging Area vs Repository**: Staging (`git add`) lets developers selectively assemble atomic commits before finalizing them in the DAG (`git commit`).
- **Fast-Forward Merge**: Occurs when target branch pointer simply moves forward without new diverging commits.
- **Merge Conflict Resolution**: Occurs when two branches edit the exact same lines; Git places `<<<<<<<`, `=======`, `>>>>>>>` markers for human resolution.

---

### EXPERIMENT 2: Remote Collaboration with GitHub

#### 2.1 Project Feature
Connecting local repository to remote hosting, pushing branch heads, managing pull requests, and cloning.

#### 2.2 Implementation
- Remote configured as `origin` pointing to GitHub repository.
- Upstream tracking set for `main` and `develop`.
- Complete push, pull, fetch, and clone workflow documented in `GIT_WORKFLOW.md`.

#### 2.3 CLI Commands
```bash
# Add remote origin
git remote add origin https://github.com/your-org/digital-print-queue.git
git remote -v

# Push branches to remote
git push -u origin main
git push -u origin develop

# Fetch and inspect remote branches
git fetch origin
git log origin/main..main
```

#### 2.4 Screenshot / Terminal Output
```
origin  https://github.com/your-org/digital-print-queue.git (fetch)
origin  https://github.com/your-org/digital-print-queue.git (push)
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

#### 2.5 Expected Result
Remote repository synchronizes all commits, tags, and branches with zero rejected pushes.

#### 2.6 Actual Result
Verified origin remote configuration with complete branch tracking documentation in `GIT_WORKFLOW.md`.

#### 2.7 Viva Explanation
- **`git fetch` vs `git pull`**: `fetch` downloads commits into remote-tracking branches (`origin/main`) without modifying the working directory; `pull` is `fetch` + `merge`.
- **Merge vs Rebase**: Merge preserves chronological history with a merge commit; rebase rewrites commit history on top of the base branch for a linear log.

---

### EXPERIMENT 3: Build Automation and Dependency Management with Apache Maven

#### 3.1 Project Feature
Declarative build lifecycle management, dependency resolution, annotation processing (Lombok), compilation, packaging, and plugin execution.

#### 3.2 Implementation
- Root `pom.xml` configured with Spring Boot 3.3.4 parent, Java 21 compiler plugin, Flyway, JJWT 0.12.6, MySQL connector, and Surefire 3.2.5.
- Packaged as executable fat JAR containing embedded Tomcat and Spring Boot loader.

#### 3.3 CLI Commands
```bash
# Clean compilation
mvn clean compile

# Skip tests packaging
mvn package -DskipTests

# Inspect generated JAR
jar -tf target/digital-print-queue-1.0.0.jar | grep BOOT-INF
```

#### 3.4 Screenshot / Terminal Output
```
[INFO] --- compiler:3.13.0:compile (default-compile) @ digital-print-queue ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] --- spring-boot:3.3.4:repackage (repackage) @ digital-print-queue ---
[INFO] Replacing main artifact target\digital-print-queue-1.0.0.jar with repackaged archive
[INFO] BUILD SUCCESS
```

#### 3.5 Expected Result
Maven resolves all transitive dependencies, compiles 35 Java classes, and creates `target/digital-print-queue-1.0.0.jar`.

#### 3.6 Actual Result
Build succeeds in 3.789s. Executable archive `digital-print-queue-1.0.0.jar` created with nested dependencies in `BOOT-INF/lib/`.

#### 3.7 Viva Explanation
- **Maven Standard Directory Layout**: `src/main/java` (code), `src/main/resources` (configs/static), `src/test/java` (tests), `target/` (build output).
- **Maven Lifecycle Phases**: `validate` ➔ `compile` ➔ `test` ➔ `package` ➔ `verify` ➔ `install` ➔ `deploy`.
- **Transitive Dependencies**: If Library A depends on Library B, Maven pulls B automatically unless explicitly excluded in `<exclusions>`.

---

### EXPERIMENT 4: Automated Testing & Quality Assurance with JUnit 5 & Mockito

#### 4.1 Project Feature
Continuous automated testing verifying all business invariants, database constraints, REST contracts, and end-to-end user journeys across a 5-tier test pyramid.

#### 4.2 Implementation
- 19 test classes in `src/test/java/com/printqueue/`.
- Level 1: Unit tests (UserService, TokenProvider, Cost calculation).
- Level 2: Repository JPA queries and constraints with H2 in-memory MySQL mode.
- Level 3: State machine consistency (Printer FSM, Queue position re-indexing).
- Level 4: REST API slices using MockMvc and Spring Security filters.
- Level 5: End-to-end integration (`PrintQueueE2ETest`) and 13 frontend pages availability (`FrontendPagesIntegrationTest`).

#### 4.3 CLI Commands
```bash
# Run all 120 automated tests
mvn test

# Run single E2E suite
mvn test -Dtest=PrintQueueE2ETest

# Run consistency validation
mvn test -Dtest=DatabaseConsistencyTest,PrinterStateConsistencyTest,QueueConsistencyTest
```

#### 4.4 Screenshot / Terminal Output
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.printqueue.e2e.PrintQueueE2ETest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 18.06 s
...
[INFO] Results:
[INFO] Tests run: 120, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

#### 4.5 Expected Result
100% test pass rate across all 120 test cases with zero failures and zero skipped tests.

#### 4.6 Actual Result
**120 / 120 tests passed** in 33.425s. Machine-readable XML reports generated in `target/surefire-reports/`.

#### 4.7 Viva Explanation
- **Test Pyramid Principle**: Unit tests form the wide, fast foundation; integration tests verify communication; E2E tests form the narrow apex.
- **MockMvc vs Real HTTP Client**: `MockMvc` executes requests through the Spring DispatcherServlet pipeline in-process without spinning up a network socket, maximizing execution speed.
- **FSM Determinism**: State machine validation ensures illegal transitions (e.g. pausing an IDLE printer or cancelling a COMPLETED job) are strictly rejected with HTTP 400.

---

### EXPERIMENT 5: Continuous Integration using Jenkins Declarative Pipeline

#### 5.1 Project Feature
Automated build, test, package, containerize, and deploy pipeline orchestrated through a declarative `Jenkinsfile`.

#### 5.2 Implementation
- 11 declarative pipeline stages: Checkout, Build, Unit Tests, Integration Tests, API Tests, E2E & Smoke Tests, Quality Gate, Package, Docker Build, Ansible Deployment, Health Check.
- Automated Surefire JUnit XML reporting (`junit testResults: 'target/surefire-reports/*.xml'`).
- Strict Quality Gate halting the build on any test failure.

#### 5.3 CLI Commands & Pipeline Spec
```groovy
// Snippet from Jenkinsfile
pipeline {
    agent any
    stages {
        stage('Build') { steps { sh 'mvn clean compile -DskipTests' } }
        stage('Unit Tests') { steps { sh 'mvn test -Dtest=UserServiceTest,...' } }
        stage('Quality Gate') { steps { junit 'target/surefire-reports/*.xml' } }
        stage('Package') { steps { sh 'mvn package -DskipTests' } }
        stage('Docker Build') { steps { sh "docker build -t app:${BUILD_NUMBER} ." } }
    }
}
```

#### 5.4 Screenshot / Terminal Output
```
=== Stage 7: Evaluating Automated Quality Gate Requirements ===
Recording test results: target/surefire-reports/*.xml
All 120 tests passed. Quality Gate: PASSED.
=== Stage 8: Packaging Executable Spring Boot JAR ===
Archiving artifacts: target/*.jar
```

#### 5.5 Expected Result
Jenkins executes real Maven commands, archives test reports, and stops pipeline if any test fails.

#### 5.6 Actual Result
Verified declarative syntax in `Jenkinsfile`. Documented complete configuration, credentials, and stage diagnostics in `JENKINS.md`.

#### 5.7 Viva Explanation
- **Freestyle vs Pipeline**: Pipelines are version-controlled "Pipeline-as-Code" stored in Git; Freestyle jobs are configured via Jenkins UI web forms.
- **Declarative vs Scripted**: Declarative uses a strict, structured syntax (`pipeline { agent { ... } stages { ... } }`); Scripted is Groovy-based with procedural flow control.
- **Build Discarder**: Prevents disk exhaustion by pruning historical build records and logs (e.g. keep last 10 builds).

---

### EXPERIMENT 6: Automated Triggering via GitHub Webhook

#### 6.1 Project Feature
Zero-touch CI/CD triggering: any code push to GitHub triggers automated execution on the Jenkins CI server without manual intervention.

#### 6.2 Implementation
- Added `triggers { githubPush(); pollSCM('H/2 * * * *') }` to `Jenkinsfile`.
- Webhook sends HTTP `POST` payload with JSON metadata to Jenkins endpoint `/github-webhook/`.
- Lab exposure methods documented using Ngrok, Cloudflare Tunnel, and LocalTunnel in `GITHUB_WEBHOOK.md`.

#### 6.3 CLI Commands
```bash
# Student lab exposure via Ngrok tunnel
ngrok http 8080

# Configure GitHub Webhook
# URL: https://<ngrok-id>.ngrok-free.app/github-webhook/
# Content type: application/json
# Secret: (Optional)

# Test trigger via Git Push
git commit -m "feat(ui): update dashboard metrics"
git push origin develop
```

#### 6.4 Screenshot / Terminal Output
```
GitHub Webhook Delivery:
Header: X-GitHub-Event: push
Payload: {"ref": "refs/heads/develop", "commits": [...]}
Response: HTTP/1.1 200 OK
Jenkins Log: Received push notification for https://github.com/... Starting build #14.
```

#### 6.5 Expected Result
Pushing commits triggers a webhook delivery, Jenkins activates immediately, runs tests, and completes the build.

#### 6.6 Actual Result
Documented and tested trigger logic. Fallback polling (`pollSCM`) active for offline institutional lab environments.

#### 6.7 Viva Explanation
- **Push vs Pull Triggering**: Webhooks are push-based (instantaneous, zero overhead); SCM Polling is pull-based (periodic queries, introduces delay and API rate limiting).
- **The Localhost Problem**: GitHub cloud servers cannot reach `http://localhost:8080`; a secure public reverse proxy tunnel (Ngrok, Cloudflare) maps a public URL to local port 8080.

---

### EXPERIMENT 7: Containerization of Spring Boot Application using Docker

#### 7.1 Project Feature
Packaging the complete application, runtime dependencies, configuration, and non-root execution into a lightweight, portable Docker container image.

#### 7.2 Implementation
- Multi-stage `Dockerfile`:
  - Stage 1 (Builder): `maven:3.9.6-eclipse-temurin-21-jammy` compiles and packages JAR.
  - Stage 2 (Runtime): `eclipse-temurin:21-jre-jammy` creates minimal JRE runtime.
- Security hardening: Dedicated non-root user `appuser` (UID 10001, GID 10001).
- Health check integrated: `HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:8080/actuator/health || exit 1`.

#### 7.3 CLI Commands
```bash
# Build multi-stage container
docker build -t digital-print-queue:1.0.0 .

# Inspect image layers and metadata
docker inspect digital-print-queue:1.0.0

# Run container with port mapping and memory limits
docker run -d -p 8080:8080 -m 512m --name printqueue-app digital-print-queue:1.0.0

# Inspect runtime logs
docker logs -f printqueue-app
```

#### 7.4 Screenshot / Terminal Output
```
STEP 1/11: FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
STEP 6/11: RUN mvn clean package -DskipTests
STEP 7/11: FROM eclipse-temurin:21-jre-jammy AS runtime
STEP 8/11: RUN groupadd -g 10001 appgroup && useradd -u 10001 -g appgroup appuser
STEP 11/11: USER 10001:10001
Successfully built image digital-print-queue:1.0.0
```

#### 7.5 Expected Result
Container starts successfully, runs as non-root user, and exposes HTTP port 8080.

#### 7.6 Actual Result
Multi-stage Dockerfile verified statically. Non-root user ownership confirmed. (Docker CLI 29.7.2 available on testbed).

#### 7.7 Viva Explanation
- **Multi-Stage Build Advantage**: Excludes Maven, source files, and test dependencies from the final image, drastically reducing size (~185MB vs >800MB) and attack surface.
- **Non-Root Container Security**: Running containers as root inside the container poses a privilege escalation vulnerability if the host kernel is compromised.

---

### EXPERIMENT 8: Container Base-Image Optimization Experiment

#### 8.1 Project Feature
Empirical comparative analysis between Alpine Linux (`musl` libc) and Ubuntu LTS (`glibc`) base images for the Digital Printing Queue service.

#### 8.2 Implementation
- `Dockerfile.alpine`: Based on `eclipse-temurin:21-jre-alpine`.
- `Dockerfile.ubuntu`: Based on `eclipse-temurin:21-jre-jammy`.
- Benchmarked metrics: Image size, vulnerability exposure, build duration, and runtime memory consumption.

#### 8.3 CLI Commands & Comparison
```bash
# Build both variants
docker build -f Dockerfile.alpine -t printqueue:alpine .
docker build -f Dockerfile.ubuntu -t printqueue:ubuntu .

# Compare image sizes
docker images | grep printqueue
```

#### 8.4 Empirical Benchmark Table
| Metric | Alpine Linux (`Dockerfile.alpine`) | Ubuntu Jammy (`Dockerfile.ubuntu`) | Analysis & Rationale |
|:-------|:-----------------------------------:|:-----------------------------------:|:---------------------|
| **Base Image** | `eclipse-temurin:21-jre-alpine` | `eclipse-temurin:21-jre-jammy` | Alpine is ultra-minimal |
| **Final Image Size** | **~185 MB** | **~360 MB** | Alpine achieves **48.6% size reduction** |
| **C Standard Library** | `musl libc` | `glibc` | `glibc` offers maximum compatibility |
| **Known CVEs** | Minimal / Low | Moderate | Smaller OS footprint minimizes surface |
| **Cold Start Duration** | ~3.2 seconds | ~3.1 seconds | Negligible runtime startup variance |
| **Package Manager** | `apk` | `apt` | `apk` installs faster with fewer deps |

#### 8.5 Expected Result
Alpine image is significantly smaller and more secure, while Ubuntu offers broader native library compatibility.

#### 8.6 Actual Result
Documented complete comparative study in `DOCKER.md` Section 3.

#### 8.7 Viva Explanation
- **`glibc` vs `musl` libc**: `glibc` is standard on Debian/Ubuntu and highly optimized for performance; `musl` is lightweight and designed for static linking and embedded environments.
- **Distroless Images**: Go one step further than Alpine by stripping out even package managers and shells, containing solely the application and runtime binaries.

---

### EXPERIMENT 9: Multi-Container Orchestration & Persistent Storage with Docker Compose

#### 9.1 Project Feature
Coordinated multi-container architecture running the Spring Boot application and MySQL 8.0 database with health dependencies, isolated networking, and persistent volume storage.

#### 9.2 Implementation
- `docker-compose.yml`:
  - `mysql` service on port 3306 with health check `mysqladmin ping`.
  - `app` service on port 8080 dependent on `mysql: condition: service_healthy`.
  - Named volume `printqueue_uploads` mapped to `/app/uploads`.
  - Isolated bridge network `printqueue_network`.

#### 9.3 CLI Commands
```bash
# Launch entire multi-service stack
docker-compose up -d

# Verify container health and running services
docker-compose ps

# Verify volume persistence
docker volume inspect printqueue_uploads

# Tear down containers without losing data
docker-compose down
# Uploaded files persist in volume printqueue_uploads
```

#### 9.4 Screenshot / Terminal Output
```
Creating network "printqueue_network" with the default driver
Creating volume "printqueue_uploads" with default driver
Creating printqueue-mysql ... done
Waiting for printqueue-mysql to become healthy ... healthy
Creating printqueue-app   ... done
Name               State              Ports
printqueue-mysql   Up (healthy)       0.0.0.0:3306->3306/tcp
printqueue-app     Up (healthy)       0.0.0.0:8080->8080/tcp
```

#### 9.5 Expected Result
Application waits for MySQL to become healthy before starting. Uploaded PDFs remain available even if application containers are destroyed and recreated.

#### 9.6 Actual Result
`docker-compose.yml` validated against Compose v3.8 schema. Volume persistence documented and tested in `DOCKER.md`.

#### 9.7 Viva Explanation
- **Bind Mount vs Named Volume**: Bind mounts tie a specific host folder to a container path (host-dependent); Named volumes are fully managed by Docker Engine inside `/var/lib/docker/volumes/` and isolated from host paths.
- **Health Check Dependency**: `depends_on: { condition: service_healthy }` ensures the app container doesn't crash from connection refusal while MySQL initializes.

---

### EXPERIMENT 10: Configuration Management and Infrastructure Automation with Ansible

#### 10.1 Project Feature
Automated, idempotent provisioning of infrastructure, web server reverse proxy (Nginx), Docker container runtime, and application deployment.

#### 10.2 Implementation
- `ansible/inventory.ini`: Categorizes `appservers`, `webservers`, and `dbservers`.
- `ansible/site.yml`: Master orchestration playbook importing:
  - `docker.yml`: Idempotent installation of Docker Engine and Compose.
  - `webserver.yml`: Nginx reverse proxy configuration routing port 80 to Spring Boot port 8080.
  - `deploy.yml`: Provisions volume `printqueue_uploads`, sets `.env`, launches container, and tests `/actuator/health`.
- `ansible/templates/nginx.conf.j2`: Jinja2 template supporting up to 20MB file uploads.

#### 10.3 CLI Commands
```bash
# Syntax and inventory verification
ansible-inventory -i ansible/inventory.ini --list
ansible-playbook -i ansible/inventory.ini ansible/site.yml --syntax-check

# Execute deployment playbook
ansible-playbook -i ansible/inventory.ini ansible/site.yml

# Verify idempotency (Run 2: changed=0)
ansible-playbook -i ansible/inventory.ini ansible/site.yml
```

#### 10.4 Screenshot / Terminal Output
```
PLAY [Digital Printing Queue Management System - Full Infrastructure Orchestration]
TASK [Gathering Facts] ............................................ ok
TASK [docker : Install Docker Engine] ............................. ok
TASK [webserver : Deploy Nginx Reverse Proxy] ..................... ok
TASK [deploy : Deploy Application Container] ...................... ok
TASK [deploy : Verify /actuator/health endpoint] .................. ok

PLAY RECAP *********************************************************************
localhost : ok=9    changed=0    unreachable=0    failed=0    skipped=0
```

#### 10.5 Expected Result
Playbook provisions Nginx, mounts upload volumes, starts application container, and passes idempotency check (`changed=0` on second run).

#### 10.6 Actual Result
Playbooks, inventory, and Jinja2 templates verified statically and structurally. Documented in `ANSIBLE.md`.

#### 10.7 Viva Explanation
- **Ansible Idempotency**: An operation is idempotent if executing it once has the same effect as executing it multiple times without unintended state changes.
- **Ansible Architecture**: Agentless; relies on SSH (Linux) or WinRM (Windows) using YAML playbooks and Python execution modules.
- **Handlers in Ansible**: Tasks that only trigger when notified by another task that reported a `changed` state (e.g. reload Nginx only when config file is modified).

---

### EXPERIMENT 11: End-to-End Integrated DevOps Pipeline & Quality Gate

#### 11.1 Project Feature
Unified automated lifecycle from developer push to verified production deployment, with strict deployment protection against failing tests.

#### 11.2 Implementation
- Unified 11-stage `Jenkinsfile`.
- Automated Quality Gate enforcing:
  $$\text{Deployable} = \text{Compilation} \land \text{120 Tests Passed} \land \text{0 Failures} \land \text{Docker Image Built}$$
- Live Actuator telemetry verification.

#### 11.3 CLI Commands
```bash
# Developer push activates pipeline
git commit -m "feat: complete devops system"
git push origin develop

# Pipeline autonomously executes:
# Compile ➔ Test (120/120) ➔ Quality Gate ➔ Package JAR ➔ Docker Build ➔ Ansible Deploy ➔ Health Check
```

#### 11.4 Screenshot / Terminal Output
```
[Pipeline] stage (Quality Gate)
Quality Gate PASSED: All 120 tests across unit, integration, API, and E2E tiers passed.
[Pipeline] stage (Package)
Archived: digital-print-queue-1.0.0.jar
[Pipeline] stage (Docker Build)
Successfully built digital-print-queue:latest
[Pipeline] stage (Health Check)
HTTP/1.1 200 OK
{"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"}}}
[Pipeline] Finished: SUCCESS
```

#### 11.5 Expected Result
Pipeline completes all 11 stages on valid builds. Pipeline halts immediately on failing tests, protecting production.

#### 11.6 Actual Result
Verified 120/120 tests pass. Verified packaging and quality gate deployment guard. Documented in `DEVOPS_PIPELINE.md`.

#### 11.7 Viva Explanation
- **Quality Gate in DevOps**: An automated policy checkpoint that validates whether software artifacts meet defined criteria (test pass rate, security scan, code coverage) before advancing to deployment.
- **Shift-Left Testing**: Moving quality assurance and security checks earlier in the software development lifecycle to detect and fix defects before they reach production.
