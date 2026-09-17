# GITHUB WEBHOOK INTEGRATION SPECIFICATION & LAB MANUAL
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Module**: Continuous Integration & Automated Triggering | **Trigger**: GitHub Webhook

---

## 1. End-to-End Trigger Architecture

The continuous integration loop is triggered automatically when a developer pushes commits to GitHub:

```
┌──────────────┐
│  Developer   │
│   git push   │
└──────┬───────┘
       │ HTTPS Push
       ▼
┌──────────────┐
│    GitHub    │
│  Repository  │
└──────┬───────┘
       │ HTTP POST (JSON Payload to /github-webhook/)
       ▼
┌──────────────┐
│   Jenkins    │
│ (Controller) │
└──────┬───────┘
       │ Starts Pipeline
       ▼
┌──────────────┐      Pass      ┌──────────────┐      Pass      ┌──────────────┐
│ Unit Tests   │───────────────►│  Integration │───────────────►│ Docker Build │
│  (Level 1-2) │                │  Tests (E2E) │                │   & Deploy   │
└──────┬───────┘                └──────┬───────┘                └──────────────┘
       │ Fail                          │ Fail
       ▼                               ▼
┌──────────────────────────────────────────────┐
│  PIPELINE HALTED — DEPLOYMENT BLOCKED        │
└──────────────────────────────────────────────┘
```

---

## 2. The `localhost` Limitation & Explanation

### 2.1 The Problem
- When Jenkins runs inside a student lab machine, its network address is `http://localhost:8081` or a private local area network IP (such as `192.168.x.x` or `10.x.x.x`).
- **GitHub's cloud servers cannot route packets to `localhost` or private RFC 1918 subnets**.
- If you enter `http://localhost:8081/github-webhook/` in GitHub's webhook settings, GitHub will fail with:
  `We couldn’t deliver this payload: Failed to connect to localhost port 8081: Connection refused`.

### 2.2 Why This Happens
- Private IP addresses and `localhost` (loopback `127.0.0.1`) are only accessible from within that specific machine or local LAN.
- GitHub requires a publicly routable, internet-accessible URL (with a valid domain or public IP).

---

## 3. Practical Student-Lab Exposure Methods

In college and university laboratory environments, students can use the following standard techniques to bridge GitHub webhooks to their local Jenkins instances:

### Method A: Ngrok Tunnel (Industry Standard for Local Testing)
[Ngrok](https://ngrok.com/) creates a secure public HTTPS tunnel to your local Jenkins port without requiring router port-forwarding or public IP addresses.

1. **Install / Run Ngrok**:
   ```bash
   # In terminal or PowerShell:
   ngrok http 8081
   ```
2. **Observe the Public Forwarding URL**:
   ```text
   Forwarding   https://a1b2-c3d4.ngrok-free.app -> http://localhost:8081
   ```
3. **Configure GitHub Webhook**:
   Use the HTTPS forwarding address with the `/github-webhook/` path:
   `https://a1b2-c3d4.ngrok-free.app/github-webhook/`

---

### Method B: Cloudflare Tunnel (Zero-Config & No Time Limit)
[Cloudflare Tunnels](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/) offer a free, persistent tunnel:
```bash
# Run one-off quick tunnel without account:
cloudflared tunnel --url http://localhost:8081
```
Copy the generated `https://<random>.trycloudflare.com` URL into GitHub Webhooks.

---

### Method C: LocalTunnel (No Sign-Up Required)
Using Node.js:
```bash
npx localtunnel --port 8081
```
Use the output URL: `https://<subdomain>.loca.lt/github-webhook/`.

---

### Method D: Poll SCM Fallback (Offline / Strict Firewall Labs)
If institutional proxy firewalls block tunneling tools, Jenkins provides an automated internal polling trigger configured in `Jenkinsfile`:

```groovy
triggers {
    // Triggers on GitHub webhook push event
    githubPush()
    // Lab fallback: checks Git repository every 2 minutes automatically
    pollSCM('H/2 * * * *')
}
```
*Benefits in student labs*: Automatically detects and builds new commits without requiring inbound public internet connectivity!

---

## 4. Step-by-Step Configuration Guide

### 4.1 Step 1: Configure Jenkins
1. Open Jenkins at `http://localhost:8081` and navigate to **Manage Jenkins** ➔ **Plugins** ➔ **Installed plugins**.
2. Verify **GitHub Plugin** and **Git Plugin** are installed and enabled.
3. Navigate to **Manage Jenkins** ➔ **System** ➔ scroll to **GitHub**:
   - Click **Add GitHub Server**.
   - Name: `GitHub Cloud`.
   - API URL: `https://api.github.com`.
   - Credentials: Select your GitHub Personal Access Token (PAT) with `repo` and `admin:repo_hook` scopes.
   - Click **Test connection** (should report: `Credentials verified for user <username>`).
4. In your Jenkins Pipeline job (`digital-print-queue-pipeline`):
   - Under **Build Triggers**, check **GitHub hook trigger for GITScm polling**.
   - Save the job configuration.

---

### 4.2 Step 2: Configure GitHub Repository Webhook
1. Open your repository on GitHub (`https://github.com/<your-username>/digital-print-queue`).
2. Click **Settings** ➔ **Webhooks** (in the left navigation) ➔ click **Add webhook**.
3. Fill in the fields:
   - **Payload URL**: `https://<your-tunnel-subdomain>.ngrok-free.app/github-webhook/`
     > ⚠️ **CRITICAL**: Note the trailing slash `/` at the end of `/github-webhook/`. Jenkins requires it!
   - **Content type**: `application/json`
   - **Secret**: *(Leave empty or provide secret matched in Jenkins)*
   - **Which events would you like to trigger this webhook?**: Select **Just the push event**.
   - **Active**: Ensure the checkbox is checked.
4. Click **Add webhook**.
5. GitHub will immediately send a test ping. A **Green Checkmark (HTTP 200)** confirms successful connectivity.

---

## 5. End-to-End Verification Walkthrough

Follow this deterministic test procedure to verify the entire automated pipeline:

### Step 1: Push a Change
Make a small documentation edit or feature commit and push to GitHub:
```bash
git switch develop
git commit --allow-empty -m "ci: test github webhook integration trigger"
git push origin develop
```

### Step 2: GitHub Processes the Push
- GitHub detects the new commit on branch `develop`.
- Under **Settings ➔ Webhooks ➔ Recent Deliveries**, a new delivery entry appears with payload details (Commit hash, author, branch ref).

### Step 3: Webhook Delivery
- GitHub makes an HTTP POST request to `https://<tunnel>/github-webhook/`.
- Response from Jenkins: `HTTP 200 OK`.

### Step 4: Jenkins Receives Trigger
- The GitHub plugin logs:
  ```text
  Received POST for https://github.com/<user>/digital-print-queue
  Connecting to https://api.github.com with ...
  Examining digital-print-queue-pipeline
  Scheduling build for digital-print-queue-pipeline
  ```

### Step 5: Pipeline Starts Automatically
- Build number (e.g., `#2`) starts automatically with cause:
  `Started by GitHub push by <username>`.

### Step 6: Test Execution & Quality Gate
- **Stage: Unit Test**: Executes all 39 unit and repository tests.
- **Stage: Integration Test**: Executes `RegressionTestSuite` with all 118 tests.

### Step 7: Conditional Docker Build & Deployment
- **Scenario A (All Tests Pass)**:
  Pipeline proceeds to **Package**, builds the Docker image, executes `docker compose up -d`, and verifies `/actuator/health`.
- **Scenario B (Test Fails)**:
  Pipeline halts immediately with status **FAILURE**. Docker build and deployment are blocked.

---

## 6. Common Pitfalls & Troubleshooting

| Issue | Root Cause | Resolution |
|-------|------------|------------|
| **HTTP 403 Forbidden** | Jenkins CSRF Protection or missing trailing slash | Ensure Payload URL ends with `/github-webhook/` and Jenkins allows webhook endpoints through CSRF filter. |
| **Connection Refused** | Attempting to use `localhost` directly in GitHub | Use an exposure tunnel like Ngrok or enable `pollSCM` fallback. |
| **Jenkins does not start build** | Job Git URL does not match GitHub payload URL | Ensure Git repository URL in Jenkins matches GitHub URL exactly. |
| **Webhook Delivery shows 404** | GitHub plugin not installed or misconfigured | Install `GitHub Integration Plugin` from Plugin Manager. |
