# GIT WORKFLOW & VERSION CONTROL SPECIFICATION
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Version**: 1.0.0 | **Branch Strategy**: GitFlow Variant

---

## 1. Branching Strategy & Architecture

The project follows a disciplined multi-branch GitFlow workflow ensuring production stability, seamless team collaboration, and isolated feature development:

```
 main (Production)
   │
   └── develop (Integration)
         ├── feature/authentication   ── (Phase 1 & 2: User auth, JWT, BCrypt)
         ├── feature/print-job        ── (Phase 3: Document uploads, pricing)
         ├── feature/queue            ── (Phase 4: Digital queue engine, FIFO + Priority)
         ├── feature/admin            ── (Phase 5 & 6: Printer FSM, Admin dashboard, audit)
         ├── feature/testing          ── (Phase 7 & 8: Frontend, E2E tests, QA suite)
         ├── feature/docker           ── (Phase 10: Dockerfile & Compose orchestration)
         └── feature/ansible          ── (Phase 11: Ansible configuration playbooks)
```

### Branch Roles & Policies
| Branch | Role | Merge Policy |
|--------|------|--------------|
| `main` | Production-ready releases | Merges only from `develop` after passing all quality gates. Protected. |
| `develop` | Integration branch for latest features | Default working branch for daily development. |
| `feature/*` | Ephemeral feature branches | Branched from `develop`, merged back to `develop` via Pull Request after passing tests. |

---

## 2. Core Git Command Reference

### 2.1 Repository Setup & Configuration
```bash
# Initialize local repository
git init

# Configure user identity
git config --global user.name "DevOps Engineer"
git config --global user.email "devops@digitalprint.com"

# Set default branch name
git config --global init.defaultBranch main
```

### 2.2 Working Tree & Staging
```bash
# Check modified/untracked files
git status

# Stage specific file or all files
git add <filename>
git add .

# Create snapshot with descriptive message
git commit -m "feat(queue): implement dynamic queue slot recalculation"

# View commit history
git log --oneline --graph --decorate
```

### 2.3 Branch Management
```bash
# List all local branches
git branch

# Create a new branch
git branch <branch-name>

# Switch branches
git switch <branch-name>
# or
git checkout <branch-name>

# Create and switch in one step
git switch -c feature/my-feature
```

### 2.4 Merging & Integration
```bash
# Merge a branch into current branch
git switch develop
git merge feature/authentication

# Fast-forward vs No-FF
git merge --no-ff feature/print-job -m "merge: integrate print-job feature"
```

### 2.5 Remote Operations
```bash
# Add remote repository
git remote add origin https://github.com/your-org/digital-print-queue.git

# Verify remote URLs
git remote -v

# Push branch to remote
git push -u origin develop

# Fetch updates without merging
git fetch origin

# Pull (fetch + merge)
git pull origin develop

# Clone repository
git clone https://github.com/your-org/digital-print-queue.git
```

---

## 3. Merge Conflict Demonstration & Resolution Workflow

### 3.1 What Causes a Merge Conflict?
A merge conflict occurs when two branches modify the **same line(s)** of a file differently, and Git cannot automatically determine which version to keep.

### 3.2 Safe Step-by-Step Merge Conflict Example

#### Step 1: Create a baseline branch
```bash
git switch develop
git switch -c demo/conflict-base
echo "System Version: 1.0.0" > version.txt
git add version.txt
git commit -m "docs: add initial version.txt"
```

#### Step 2: Branch A modifies line 1
```bash
git switch -c demo/branch-alpha
echo "System Version: 1.1.0-alpha (Feature Alpha)" > version.txt
git add version.txt
git commit -m "feat(alpha): bump version to alpha"
```

#### Step 3: Branch B modifies line 1 differently
```bash
git switch demo/conflict-base
git switch -c demo/branch-beta
echo "System Version: 1.2.0-beta (Feature Beta)" > version.txt
git add version.txt
git commit -m "feat(beta): bump version to beta"
```

#### Step 4: Merge Branch Alpha into Base (Clean)
```bash
git switch demo/conflict-base
git merge demo/branch-alpha
# Merge succeeds via Fast-Forward or clean merge commit
```

#### Step 5: Merge Branch Beta into Base (Conflict Triggered!)
```bash
git merge demo/branch-beta
# Output:
# Auto-merging version.txt
# CONFLICT (content): Merge conflict in version.txt
# Automatic merge failed; fix conflicts and then commit the result.
```

#### Step 6: Inspect the Conflict Marker
Opening `version.txt` reveals Git's conflict markers:
```text
<<<<<<< HEAD
System Version: 1.1.0-alpha (Feature Alpha)
=======
System Version: 1.2.0-beta (Feature Beta)
>>>>>>> demo/branch-beta
```

#### Step 7: Resolve and Finalize
Edit `version.txt` to the reconciled value:
```text
System Version: 1.2.0 (Integrated Alpha & Beta)
```

Stage and commit the resolution:
```bash
git add version.txt
git commit -m "merge: resolve version.txt conflict between alpha and beta"
```

---

## 4. Academic Project Branch Verification

All required project branches have been created and indexed:
1. `main`
2. `develop`
3. `feature/authentication`
4. `feature/print-job`
5. `feature/queue`
6. `feature/admin`
7. `feature/testing`
8. `feature/docker`
9. `feature/ansible`
