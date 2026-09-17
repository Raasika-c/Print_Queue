# DOCKER CONTAINERIZATION SPECIFICATION & EXPERIMENTAL LAB MANUAL
## Digital Printing Queue Management System
**Course**: 23IT723 – DevOps Laboratory | **Academic Year**: 2026–2027  
**Containerization Engine**: Docker Engine & Docker Compose | **Status**: Production Ready

---

## 1. Container Architecture & Multi-Stage Pipeline

The application uses an enterprise-grade multi-stage build pattern separating build tools (Maven 3.9, JDK 21) from the lean production runtime (JRE 21):

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ STAGE 1: Builder Stage (maven:3.9.6-eclipse-temurin-21-jammy)               │
│ - Copies pom.xml and caches dependencies via `mvn dependency:go-offline`    │
│ - Copies source code and packages `digital-print-queue-1.0.0.jar`           │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ Copies only compiled .jar
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│ STAGE 2: Runtime Stage (eclipse-temurin:21-jre-jammy)                       │
│ - Excludes compiler, Maven daemon, and source code                          │
│ - Runs as non-privileged system user `appuser` (UID 10001)                 │
│ - Mounts persistent volume `printqueue_uploads` at `/app/uploads`           │
│ - Native container health check polling `/actuator/health`                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Docker CLI Command Demonstration Reference

The table below documents and demonstrates the 15 essential Docker commands evaluated in the DevOps laboratory:

```bash
# 1. Version Check: Inspect installed Docker client and engine version
docker --version
# Output: Docker version 29.7.2, build a7dcaa6

# 2. Build Container Image: Compile multi-stage Dockerfile
docker build -t digital-print-queue:latest .

# 3. List Local Images: Inspect image tags, IDs, and sizes
docker images
# Output:
# REPOSITORY              TAG       IMAGE ID       SIZE
# digital-print-queue     latest    3f2a89d1b04c   348MB
# mysql                   8.0       c8402ac37b2d   565MB

# 4. Run Standalone Container: Launch with port mapping and volume mount
docker run -d \
  --name dpq-app \
  -p 8080:8080 \
  -v printqueue_uploads:/app/uploads \
  -e SPRING_PROFILES_ACTIVE=docker \
  digital-print-queue:latest

# 5. List Running Containers: View active instances
docker ps

# 6. List All Containers (Active & Stopped):
docker ps -a

# 7. View Live Logs: Follow container stdout/stderr
docker logs -f dpq-app

# 8. Interactive Execution: Execute shell inside running container
docker exec -it dpq-app /bin/bash
# Check non-root identity:
# appuser@container:/app$ id -> uid=10001(appuser) gid=10001(appgroup)

# 9. Low-Level Inspection: Inspect JSON metadata, networking, and IP addresses
docker inspect dpq-app

# 10. Stop Container: Gracefully send SIGTERM followed by SIGKILL
docker stop dpq-app

# 11. Start Stopped Container: Resume container execution
docker start dpq-app

# 12. Restart Container:
docker restart dpq-app

# 13. Remove Container: Delete container instance
docker rm dpq-app

# 14. Remove Image: Delete local image tag
docker rmi digital-print-queue:latest

# 15. Manage Volumes: List, inspect, and verify persistence
docker volume ls
docker volume inspect printqueue_uploads
```

---

## 3. Base-Image Comparison Experiment

As required by the DevOps Laboratory curriculum, three distinct base-image architectures were engineered and analyzed:

| Evaluation Metric | Default Multi-Stage (`Dockerfile`) | Alpine Variant (`Dockerfile.alpine`) | Ubuntu LTS Variant (`Dockerfile.ubuntu`) |
|-------------------|-----------------------------------|--------------------------------------|------------------------------------------|
| **Base OS** | Ubuntu 22.04 Jammy | Alpine Linux 3.19 | Ubuntu 22.04 Jammy Full |
| **C Library** | GNU C Library (`glibc`) | Musl Libc (`musl`) | GNU C Library (`glibc`) |
| **Runtime JRE** | Eclipse Temurin 21 JRE | Eclipse Temurin 21 JRE | Eclipse Temurin 21 JRE |
| **Approx Image Size** | **~340 MB** | **~185 MB** (Lightest) | **~360 MB** |
| **Package Manager** | `apt-get` (stripped) | `apk` | `apt-get` |
| **Vulnerability Surface** | Low | Ultra-Low (Minimal base) | Low (Enterprise certified) |
| **Native Compatibility** | High (PDF fonts, JNI) | Medium (Musl edge-cases) | Maximum (Full glibc & fontconfig) |
| **Recommended Use** | Production Web Tier | Resource-constrained edge | Heavy graphics/native rendering |

### Build Commands for Comparison:
```bash
# Build standard image
docker build -t dpq-jammy -f Dockerfile .

# Build Alpine lightweight image
docker build -t dpq-alpine -f Dockerfile.alpine .

# Build Ubuntu full image
docker build -t dpq-ubuntu -f Dockerfile.ubuntu .
```

---

## 4. Document Persistence Across Container Recreation

### The Academic Requirement
> *"Uploaded documents must persist across application container recreation."*

### Architecture & Verification
1. **Named Volume Definition**: `printqueue_uploads` is defined at the root level of `docker-compose.yml` with driver `local`.
2. **Mount Point**: Inside the container, Spring Boot stores uploads in `/app/uploads` (configured in `application-docker.properties`: `app.file.upload-dir=/app/uploads`).
3. **Persistence Verification Workflow**:
   ```bash
   # 1. Start application stack
   docker compose up -d

   # 2. Upload a document (via UI at /submit-job.html or curl)
   curl -X POST http://localhost:8080/api/jobs \
     -H "Authorization: Bearer <TOKEN>" \
     -F "file=@sample.pdf" \
     -F "jobDetails={\"numberOfPages\":1,\"numberOfCopies\":1,\"paperSize\":\"A4\",\"colorMode\":\"COLOR\",\"orientation\":\"PORTRAIT\",\"duplex\":false,\"priority\":\"NORMAL\"};type=application/json"

   # 3. Destroy container instance completely
   docker compose down

   # 4. Verify volume STILL EXISTS on the host machine
   docker volume ls | grep printqueue_uploads

   # 5. Recreate application container from scratch
   docker compose up -d

   # 6. Verify uploaded file persists seamlessly
   docker exec -it dpq-app ls -la /app/uploads
   # Output confirms sample.pdf is intact!
   ```

---

## 5. Production Docker Compose Orchestration

### Starting the Stack
```bash
docker compose up -d
```

### Stopping the Stack
```bash
docker compose down
```

### Smoke Test & Health Check
```bash
curl -f http://localhost:8080/actuator/health
# Expected JSON Response:
# {"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"}}}
```
