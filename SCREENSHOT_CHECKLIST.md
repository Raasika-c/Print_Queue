# PRACTICAL LAB SCREENSHOT CHECKLIST
## Course: 23IT723 – DevOps Laboratory | Academic Year: 2026–2027
### Digital Printing Queue Management System
**Record Book & Practical Viva Preparation**: Complete list of essential screenshots required for the laboratory observation notebook and practical examination file.

---

## Required Screenshot Inventory

| # | Experiment Category | Exact UI View / Terminal Command | What to Capture in the Screenshot | Importance |
|:--|:--------------------|:---------------------------------|:----------------------------------|:----------:|
| **1** | **Git Local** | `git status` & `git branch -a` | Clean working tree showing all 10 branches (`main`, `develop`, feature branches) | Required |
| **2** | **Git History** | `git log --oneline --graph -n 8` | Clean commit graph with conventional commits and release tags | Required |
| **3** | **Git Conflict** | `git merge conflict-demo` | Terminal displaying `<<<<<<<`, `=======`, `>>>>>>>` markers and resolution | Required |
| **4** | **Maven Build** | `mvn clean compile` | Maven output showing `BUILD SUCCESS` compiling Java 21 classes | Required |
| **5** | **Automated Tests** | `mvn test` | Surefire output showing `Tests run: 120, Failures: 0, Errors: 0, Skipped: 0` | Required |
| **6** | **Surefire Report** | `target/site/surefire-report.html` | Browser view of the generated HTML test execution report | Required |
| **7** | **Frontend Landing** | `http://localhost:8080/index.html` | Landing page showing live queue overview and system metrics | Required |
| **8** | **Registration** | `http://localhost:8080/register.html` | Registration form enforcing password complexity rules | Required |
| **9** | **User Login** | `http://localhost:8080/login.html` | Login page with demo admin credentials pre-filled | Required |
| **10** | **Submit Job** | `http://localhost:8080/submit-job.html` | Multipart file upload form showing live dynamic cost calculation preview | Required |
| **11** | **Active Queue** | `http://localhost:8080/queue.html` | Queue table displaying position `#1`, job number `DPQ-2026-XXXX`, priority | Required |
| **12** | **Job Details** | `http://localhost:8080/job-details.html?id=1` | Job inspector with live progress bar and `print_job_history` audit trail | Required |
| **13** | **Admin Dashboard** | `http://localhost:8080/admin-dashboard.html` | Admin console displaying live counts across all 10 system metrics | Required |
| **14** | **Virtual Printer** | `http://localhost:8080/printer.html` | Hardware console showing `PRINTER-01` in `PRINTING` state with progress % | Required |
| **15** | **Actuator Health** | `http://localhost:8080/actuator/health` | Browser or curl output displaying `{"status":"UP", "components":{...}}` | Required |
| **16** | **Docker Build** | `docker build -t digital-print-queue:latest .` | Terminal showing multi-stage build stages and non-root `appuser` creation | Required |
| **17** | **Docker Compose** | `docker-compose ps` | Running services `app` (8080) and `mysql` (3306) in healthy state | Required |
| **18** | **Docker Volume** | `docker volume inspect printqueue_uploads` | Volume metadata showing persistent mount path on host | Required |
| **19** | **Base OS Compare** | `docker images \| grep printqueue` | Comparative terminal output showing Alpine (~185MB) vs Ubuntu (~360MB) | Required |
| **20** | **Jenkins Pipeline** | Jenkins Web UI: Pipeline Stage View | Green pipeline view showing all 11 stages completed successfully | Required |
| **21** | **Jenkins Failure** | Jenkins Web UI: Failed Build View | Red build view showing test failure halting downstream deployment stages | Required |
| **22** | **GitHub Webhook** | GitHub Repo Settings ➔ Webhooks | Webhook delivery log showing green checkmark and `HTTP 200 OK` response | Required |
| **23** | **Ansible Playbook** | `ansible-playbook -i ansible/inventory.ini site.yml` | Terminal output showing execution recap (`changed=4` on Run 1) | Required |
| **24** | **Ansible Idempotent**| `ansible-playbook -i ansible/inventory.ini site.yml` | Second execution showing idempotency proof (`changed=0`) | Required |
