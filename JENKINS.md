# JENKINS CI/CD PIPELINE SPECIFICATION & MANUAL
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Pipeline**: Declarative Jenkinsfile | **Artifact**: `digital-print-queue.jar`

---

## 1. Jenkins Architecture & CI/CD Stages

The automated delivery pipeline is defined in `Jenkinsfile` and executes 8 deterministic stages:

```
┌──────────┐   ┌─────────┐   ┌───────────┐   ┌──────────────────┐
│ Checkout │──►│  Build  │──►│ Unit Test │──►│ Integration Test │
└──────────┘   └─────────┘   └───────────┘   └──────────────────┘
                                                       │
┌──────────────┐   ┌──────────────┐   ┌──────────────┐ │
│ Health Check │◄──│    Deploy    │◄──│ Docker Build │◄┘
│   (Actuator) │   │ (Compose Up) │   │  (Container) │
└──────────────┘   └──────────────┘   └──────────────┘
```

### Stage Summary
| Stage | Command / Action | Quality Gate Rule |
|-------|------------------|-------------------|
| **1. Checkout** | `checkout scm` | Polls Git SCM or triggers on webhook push |
| **2. Build** | `mvn clean compile -DskipTests` | Validates syntax, Lombok bytecode, and dependencies |
| **3. Unit Test** | `mvn test -Dtest=...` | Executes Level 1 & Level 2 unit and repository tests |
| **4. Integration Test** | `mvn test -Dtest=RegressionTestSuite` | Executes Level 3, 4, 5 API, consistency, and E2E flows |
| **5. Package** | `mvn package -DskipTests` | Produces standalone executable `.jar` in `target/` |
| **6. Docker Build** | `docker build -t digital-print-queue:...` | Creates production container image |
| **7. Deploy** | `docker compose up -d` | Launches MySQL and Application microservices |
| **8. Health Check** | `GET /actuator/health` | Verifies HTTP 200 with `{"status":"UP"}` |

---

## 2. Jenkins Server Setup & Tool Configuration

### 2.1 Prerequisites & Installation
1. Download Jenkins WAR or run via Docker:
   ```bash
   docker run -d -p 8081:8080 -p 50000:50000 --name jenkins -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts-jdk21
   ```
2. Complete Initial Setup Wizard: Unlock Jenkins using `/var/jenkins_home/secrets/initialAdminPassword` and install recommended plugins:
   - **Git Plugin**
   - **Pipeline**
   - **JUnit Plugin**
   - **Docker Pipeline**

### 2.2 Global Tool Configuration (`Manage Jenkins` ➔ `Tools`)
- **JDK Configuration**:
  - Name: `Java21`
  - JAVA_HOME: `C:\Program Files\Java\jdk-21.0.11` (or `/usr/lib/jvm/java-21-openjdk`)
- **Maven Configuration**:
  - Name: `Maven3`
  - MAVEN_HOME: `C:\apache-maven-3.9.16` (or `/usr/share/maven`)
- **Git Configuration**:
  - Path to Git executable: `git` (or `C:\Program Files\Git\bin\git.exe`)

---

## 3. Jenkins Job Creation Options

### Option A: Pipeline Project (Recommended)
1. In Jenkins Dashboard, click **New Item**.
2. Enter Item Name: `digital-print-queue-pipeline`.
3. Select **Pipeline** and click **OK**.
4. Scroll to **Pipeline** configuration:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/your-repo/digital-print-queue.git`
   - Branch Specifier: `*/develop` (or `*/main`)
   - Script Path: `Jenkinsfile`
5. Click **Save** and **Build Now**.

### Option B: Freestyle Project
1. Select **Freestyle project** with name `digital-print-queue-freestyle`.
2. Source Code Management: Choose `Git` and provide repo URL.
3. Build Triggers: Check `GitHub hook trigger for GITScm polling`.
4. Build Steps:
   - Invoke top-level Maven targets: `clean compile test package`
   - Execute Shell / Batch: `docker build -t digital-print-queue:latest .`
5. Post-build Actions:
   - Publish JUnit test result report: `target/surefire-reports/*.xml`

---

## 4. GitHub Webhook Integration & Credentials

1. In Jenkins, go to **Manage Jenkins** ➔ **Credentials** ➔ **Global credentials**.
2. Add Credential:
   - Kind: `Username with password` (or GitHub Personal Access Token `Secret text`)
   - ID: `github-credentials`
3. In your GitHub Repository:
   - Settings ➔ Webhooks ➔ Add webhook.
   - Payload URL: `http://<your-jenkins-ip>:8081/github-webhook/`
   - Content type: `application/json`
   - Which events: `Just the push event`.

---

## 5. Automated Test Reporting & History

- The `post { always { junit 'target/surefire-reports/*.xml' } }` step automatically parses test executions.
- **Trend Graphs**: Jenkins charts test count, pass/fail ratios, and duration across build history numbers (#1, #2, #3...).
- Test failures pinpoint exact stack traces and assertion values directly in the Jenkins web UI.

---

## 6. Failure Verification & Deployment Guard

### 6.1 Successful Build Run
- All 118 unit, repository, service, consistency, frontend, and E2E tests pass.
- Build continues through Packaging, Docker image generation, container deployment, and health checking.
- Final Status: **SUCCESS (Blue / Green)**.

### 6.2 Intentional Failure Demonstration (Verification)
To verify that downstream deployment is strictly blocked upon test failure:
1. Temporarily modify a test assertion (e.g., in `DatabaseConsistencyTest.java`, expect `user1.getId() == null`).
2. Trigger Jenkins build.
3. **Observation**:
   - Stage `Unit Test` or `Integration Test` turns **RED**.
   - Pipeline execution **STOPS IMMEDIATELY**.
   - Stages `Package`, `Docker Build`, `Deploy`, and `Health Check` are **ABORTED / SKIPPED**.
   - No broken Docker image is tagged or pushed.
   - Production container remains untouched and uninterrupted.
   - Status: **FAILED (Red)**.
