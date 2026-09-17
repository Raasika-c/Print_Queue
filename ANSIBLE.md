# ANSIBLE CONFIGURATION MANAGEMENT & AUTOMATION MANUAL
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Engine**: Ansible Core 2.15+ | **Orchestration**: Infrastructure as Code (IaC)

---

## 1. Automation Architecture & Playbook Structure

The Ansible automation directory structure encapsulates full provisioning, container deployment, web server configuration, and health monitoring:

```
ansible/
├── inventory.ini           # Node inventory (appservers, webservers, dbservers)
├── site.yml                # Master entry point orchestrating all modular playbooks
├── docker.yml              # Docker CE & Compose setup and version verification
├── webserver.yml           # Nginx installation, site configuration, and reload handlers
├── deploy.yml              # Microservice container deployment, volumes, and healthcheck
└── templates/
    └── nginx.conf.j2       # Jinja2 parameterized Nginx reverse proxy template
```

---

## 2. Core Concepts & Implementation Breakdown

### 2.1 Inventory (`inventory.ini`)
Defines target hosts grouped by operational tier (`[appservers]`, `[webservers]`, `[dbservers]`) and establishes group variables (`all:vars`):
- `app_port`: 8080 (Spring Boot port)
- `web_port`: 80 (Nginx external gateway)
- `mysql_port`: 3306 (Internal database port)
- `deploy_dir`: `/opt/digital-print-queue` (Base directory for Docker Compose and `.env`)

### 2.2 Playbook (`site.yml`)
The master orchestration blueprint importing playbooks sequentially:
1. `docker.yml` ➔ Ensures container runtime is ready.
2. `deploy.yml` ➔ Provisions persistent volumes, environment, launches containers, and runs automated health checks.
3. `webserver.yml` ➔ Configures Nginx reverse proxy routing traffic from port 80 to 8080.

### 2.3 Tasks
Self-contained, atomic operations leveraging built-in idempotent modules:
- `ansible.builtin.package`: Installs Nginx, Docker, prerequisites.
- `ansible.builtin.template`: Deploys Jinja2 Nginx configuration with variable substitution.
- `ansible.builtin.file`: Ensures directories and symlinks exist with correct permissions (`0755`, UID `10001`).
- `community.docker.docker_volume`: Idempotently ensures `printqueue_uploads` and `printqueue_mysql_data` volumes exist.
- `ansible.builtin.uri`: Queries `/actuator/health` with retry logic until status `200 OK` and `UP` is returned.

### 2.4 Variables (`vars` & `inventory.ini`)
Centralizes configuration parameters, credentials, and tuning flags across environments (dev/staging/production) without editing task definitions.

### 2.5 Handlers (`handlers:`)
Special tasks executed **only when notified** by a task that made a change:
```yaml
handlers:
  - name: Reload Nginx
    ansible.builtin.service:
      name: nginx
      state: reloaded
```
If the Nginx configuration file did not change, `Reload Nginx` is **not** called.

---

## 3. Idempotency & Repeatable Execution

### What is Idempotency?
An operation is **idempotent** if performing it multiple times yields the exact same state without producing unintended side effects or recreating running resources.

### Verification Experiment:
Run the master playbook twice sequentially:

#### First Run (`Initial Provisioning`):
```bash
ansible-playbook -i inventory.ini site.yml
```
- **Result**: Packages installed (`changed=1`), directories created (`changed=1`), volumes created (`changed=1`), containers started (`changed=1`).
- **Summary**: `ok=12  changed=6  unreachable=0  failed=0`

#### Second Run (`Idempotency Proof`):
```bash
ansible-playbook -i inventory.ini site.yml
```
- **Result**: Packages already present (`ok`), directories already exist (`ok`), Nginx config unchanged (`ok`), Docker volumes already present (`ok`), containers already running (`ok`).
- **Summary**: `ok=12  changed=0  unreachable=0  failed=0`
- **Proof**: Running the playbook a second time does **not** restart Nginx, does **not** wipe the database volume, and does **not** interrupt running print simulation tasks!

---

## 4. Docker Management & Persistent Volume Provisioning

The `deploy.yml` playbook explicitly creates and binds the required persistent storage volume:
```yaml
- name: Create persistent storage volume for user document uploads
  community.docker.docker_volume:
    name: printqueue_uploads
    state: present
```
- Ensures that user uploaded documents (`/app/uploads`) survive host reboots, application upgrades, and container recreation.
- Matches the Spring Boot container specification established in Phase 12.

---

## 5. Execution & Verification Workflow

```
ansible-playbook -i inventory.ini site.yml
              │
              ▼
   Docker Environment Verified
              │
              ▼
  Containers Deployed via Compose
              │
              ▼
  Persistent Volumes Mounted
              │
              ▼
  Nginx Reverse Proxy Configured
              │
              ▼
  Actuator Health Verification (HTTP 200 "UP")
              │
              ▼
  Full Maven Regression Suite Executed
```

### Command Reference:
```bash
# Syntax check playbooks
ansible-playbook -i inventory.ini site.yml --syntax-check

# Dry-run execution (check mode)
ansible-playbook -i inventory.ini site.yml --check

# Full execution
ansible-playbook -i inventory.ini site.yml
```
