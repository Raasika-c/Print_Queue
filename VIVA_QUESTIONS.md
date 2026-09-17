# COMPREHENSIVE VIVA VOCE QUESTIONS & ANSWERS
## Course: 23IT723 – DevOps Laboratory | Academic Year: 2026–2027
### Digital Printing Queue Management System
**Exhaustive Question Bank**: 105 Technical Questions & Model Answers covering Full-Stack Architecture, Spring Boot, MySQL, Security, Testing, Git, Jenkins, Docker, Ansible, and DevOps Engineering.

---

## Table of Contents
1. [Section 1: Java, Spring Boot & Application Architecture (Q1–Q15)](#section-1-java-spring-boot--application-architecture)
2. [Section 2: Database, JPA, Hibernate & Flyway Migrations (Q16–Q25)](#section-2-database-jpa-hibernate--flyway-migrations)
3. [Section 3: Spring Security, JWT & Authentication (Q26–Q35)](#section-3-spring-security-jwt--authentication)
4. [Section 4: Testing Pyramid, JUnit 5 & Mockito (Q36–Q48)](#section-4-testing-pyramid-junit-5--mockito)
5. [Section 5: Version Control Systems & Git / GitHub (Q49–Q60)](#section-5-version-control-systems--git--github)
6. [Section 6: Continuous Integration & Jenkins CI/CD (Q61–Q72)](#section-6-continuous-integration--jenkins-cicd)
7. [Section 7: Containerization & Docker (Q73–Q85)](#section-7-containerization--docker)
8. [Section 8: Configuration Management & Ansible (Q86–Q95)](#section-8-configuration-management--ansible)
9. [Section 9: DevOps Principles, SRE & System Design (Q96–Q105)](#section-9-devops-principles-sre--system-design)

---

## Section 1: Java, Spring Boot & Application Architecture

#### Q1: What is Spring Boot and how does it differ from the standard Spring Framework?
**Answer**: Spring Framework provides core dependency injection and modular components, but requires extensive XML or Java-based boilerplate configuration. Spring Boot is an opinionated extension that eliminates boilerplate through auto-configuration, starter POMs, embedded servlet containers (Tomcat/Jetty), and production-ready features (Actuator telemetry).

#### Q2: What is the purpose of `@SpringBootApplication` in `PrintQueueApplication.java`?
**Answer**: It is a composite annotation combining:
- `@Configuration`: Declares the class as a source of Spring bean definitions.
- `@EnableAutoConfiguration`: Automatically configures beans based on classpath dependencies.
- `@ComponentScan`: Automatically scans for components, services, and repositories in the package and its subpackages.

#### Q3: What is Spring Boot auto-configuration and how does it work?
**Answer**: Auto-configuration inspects JAR dependencies on the classpath, system properties, and defined beans at startup. Using conditional annotations (e.g. `@ConditionalOnClass`, `@ConditionalOnMissingBean`), it automatically instantiates standard configurations (like HikariCP DataSource when `mysql-connector-j` is present).

#### Q4: Why is `@EnableScheduling` used in this project?
**Answer**: `@EnableScheduling` activates Spring's background task scheduling capability, allowing methods annotated with `@Scheduled` (such as background queue dispatchers and cleanup tasks) to execute on a configured thread pool.

#### Q5: What is Spring's Inversion of Control (IoC) and Dependency Injection (DI)?
**Answer**: IoC is a design principle where object creation and lifecycle control are inverted from application code to a framework container. Dependency Injection is the mechanism used to implement IoC: dependencies are passed (injected) into collaborating objects via constructor, setter, or field injection.

#### Q6: Why is constructor injection preferred over field injection with `@Autowired`?
**Answer**: Constructor injection:
- Makes dependencies explicit and immutable (via `final` fields).
- Simplifies unit testing by allowing direct passing of mocks without reflection.
- Prevents `NullPointerException` during object instantiation.
- Detects circular dependencies at compile/startup time.

#### Q7: What role does Project Lombok play in this codebase?
**Answer**: Lombok uses compile-time annotation processing to automatically generate getters, setters, constructors (`@RequiredArgsConstructor`, `@AllArgsConstructor`), builders (`@Builder`), and loggers (`@Slf4j`), reducing repetitive Java boilerplate without runtime performance overhead.

#### Q8: Explain the Controller-Service-Repository architectural pattern used in the project.
**Answer**:
- **Controller**: Handles HTTP requests, deserializes input DTOs, triggers validation, and formats HTTP responses.
- **Service**: Implements core business logic, transaction management (`@Transactional`), and orchestrates repositories.
- **Repository**: Encapsulates data persistence and database operations via Spring Data JPA.

#### Q9: What is the difference between DTOs and JPA Entities?
**Answer**: Entities represent persistent relational database tables. Data Transfer Objects (DTOs) represent the contract between client and server for requests and responses. Separating them prevents exposing database schemas, avoids over-fetching or circular references, and enforces distinct validation boundaries.

#### Q10: How does `GlobalExceptionHandler` handle application errors?
**Answer**: Annotated with `@RestControllerAdvice`, it intercepts exceptions thrown across any controller (e.g., `MethodArgumentNotValidException`, `ResourceNotFoundException`, `BadRequestException`) and maps them into a standard, structured JSON error response (`ApiError`) with HTTP status codes and timestamps.

#### Q11: What is the purpose of Spring Boot Actuator?
**Answer**: Actuator exposes operational production-ready endpoints (such as `/actuator/health`, `/actuator/info`, `/actuator/metrics`) to monitor application availability, disk space, and database connection pools in real time.

#### Q12: How are application settings externalized across environments?
**Answer**: Externalization is achieved via `application.properties`, environment variable interpolation (`${DB_HOST:localhost}`), and Spring profiles (e.g., `application-docker.properties` activated via `SPRING_PROFILES_ACTIVE=docker`).

#### Q13: How does the virtual printer simulation operate without blocking HTTP requests?
**Answer**: In `PrintingSimulationService`, print job execution is dispatched asynchronously using Java's `CompletableFuture.runAsync()`. The HTTP controller immediately returns a 200 response while the background thread handles page-by-page progress and state transitions.

#### Q14: What is the difference between `@Component`, `@Service`, and `@Repository`?
**Answer**: All three are Spring stereotype annotations managed by the IoC container. `@Component` is the generic stereotype; `@Service` designates business logic layers; `@Repository` encapsulates persistence and provides automatic translation of database-specific exceptions into Spring's `DataAccessException` hierarchy.

#### Q15: What is the lifecycle of a Spring Bean?
**Answer**: Instantiation ➔ Populating properties/dependencies ➔ BeanNameAware/BeanFactoryAware callbacks ➔ Pre-initialization (`BeanPostProcessor`) ➔ `@PostConstruct` / InitializingBean ➔ Post-initialization (`BeanPostProcessor`) ➔ Ready for use ➔ `@PreDestroy` / DisposableBean upon context shutdown.

---

## Section 2: Database, JPA, Hibernate & Flyway Migrations

#### Q16: What is the difference between JPA and Hibernate?
**Answer**: JPA (Jakarta Persistence API) is a specification defining interfaces and standard object-relational mapping annotations. Hibernate is the concrete ORM implementation that implements the JPA specification.

#### Q17: What is Flyway and why is database migration essential in DevOps?
**Answer**: Flyway is an open-source database migration tool that executes versioned SQL scripts (`V1__...sql`, `V2__...sql`) against a database. In DevOps, it ensures database schema changes are version-controlled, repeatable, and synchronized across development, CI/CD pipelines, and production without manual DBA intervention.

#### Q18: What does Flyway's `flyway_schema_history` table do?
**Answer**: It records metadata for every applied migration script, including script name, version, description, execution timestamp, execution time, and SHA-256 checksum to prevent undetected schema drift or tampering.

#### Q19: Explain the relationship between `User`, `PrintJob`, and `PrintJobHistory` entities.
**Answer**:
- `User` to `PrintJob`: One-to-Many (`@OneToMany`). One user submits multiple print jobs.
- `PrintJob` to `User`: Many-to-One (`@ManyToOne`) with Foreign Key `user_id`.
- `PrintJob` to `PrintJobHistory`: One-to-Many (`@OneToMany`). Each print job records multiple lifecycle transition events.

#### Q20: What is the difference between `GenerationType.IDENTITY` and `GenerationType.AUTO`?
**Answer**: `IDENTITY` relies on an auto-increment database column (native to MySQL). `AUTO` delegates strategy selection to the persistence provider, which may use a separate database sequence or table generator depending on the dialect.

#### Q21: What is the purpose of `@Transactional` annotation?
**Answer**: It defines a transactional boundary around a method. Spring starts a database transaction before the method executes and commits it upon successful return. If an unchecked exception (`RuntimeException`) is thrown, Spring automatically rolls back the transaction, preserving data integrity.

#### Q22: What is the N+1 select problem in JPA and how is it resolved?
**Answer**: It occurs when fetching $N$ entities results in 1 query to fetch the parents and $N$ additional queries to fetch their associated child collections. It is resolved using `JOIN FETCH` queries, `@EntityGraph`, or configuring appropriate batch fetching sizes (`hibernate.default_batch_fetch_size`).

#### Q23: Why do we use H2 in MySQL mode for testing rather than an external MySQL server?
**Answer**: H2 in-memory mode runs embedded in the JVM process during `mvn test`. It creates an isolated, ephemeral database that boots in milliseconds, requires zero external infrastructure setup, and guarantees deterministic, independent test execution.

#### Q24: What is the purpose of `@Column(unique = true)` and how is it enforced?
**Answer**: It declares a unique constraint on the column (e.g. `email` in `users`). Flyway or Hibernate generates a database `UNIQUE KEY` constraint, causing the database engine to reject duplicate values with a `DataIntegrityViolationException`.

#### Q25: What is the difference between Lazy and Eager loading?
**Answer**: Eager loading (`FetchType.EAGER`) fetches child associations immediately with the parent entity using a JOIN. Lazy loading (`FetchType.LAZY`) defers fetching child records until they are explicitly accessed in code, reducing memory consumption and initial query execution time.

---

## Section 3: Spring Security, JWT & Authentication

#### Q26: What is JWT (JSON Web Token) and what are its three parts?
**Answer**: JWT is a compact, URL-safe standard (RFC 7519) for transmitting claims securely between parties. It consists of three Base64URL-encoded parts separated by periods:
1. **Header**: Token type (`JWT`) and signing algorithm (`HS384`/`HS512`).
2. **Payload**: Claims (subject/email, issued-at, expiration, roles).
3. **Signature**: Cryptographic hash of Header + Payload using the secret key.

#### Q27: How does stateless JWT authentication work in this application?
**Answer**:
1. User POSTs email/password to `/api/auth/login`.
2. Server validates credentials against the database.
3. Server generates a signed JWT and returns it to the client.
4. Client stores the token (e.g. `localStorage`) and attaches it as `Authorization: Bearer <token>` on subsequent requests.
5. `JwtAuthenticationFilter` validates signature and expiration on each request and populates the `SecurityContext`.

#### Q28: Why is stateless authentication preferred over session-based authentication in modern DevOps/Cloud architectures?
**Answer**: Stateless authentication eliminates server-side session state. Any application container or replica behind a load balancer can independently validate the token using the shared secret key without requiring sticky sessions or distributed session caches (like Redis).

#### Q29: What hashing algorithm is used for user passwords in this system?
**Answer**: **BCrypt** with strength factor 12 (`BCryptPasswordEncoder(12)`). BCrypt is an adaptive key derivation function with an internal salt and configurable work factor that protects against rainbow table and brute-force GPU attacks.

#### Q30: How does `JwtAuthenticationFilter` intercept HTTP requests?
**Answer**: Extending `OncePerRequestFilter`, it executes once per HTTP request before Spring's `UsernamePasswordAuthenticationFilter`. It parses the `Authorization` header, extracts the token, validates the signature via `JwtTokenProvider`, loads user details, and sets an `Authentication` object into `SecurityContextHolder`.

#### Q31: What is the role of `JwtAuthenticationEntryPoint`?
**Answer**: It implements `AuthenticationEntryPoint`. When an unauthenticated user attempts to access a protected endpoint, it intercepts the exception and sends an immediate HTTP `401 Unauthorized` response with a JSON error payload instead of redirecting to an HTML login page.

#### Q32: How is Role-Based Access Control (RBAC) enforced in the controllers?
**Answer**: It is enforced using method-level security with `@PreAuthorize("hasRole('ADMIN')")` enabled by `@EnableMethodSecurity`. Spring Security intercepts invocations and verifies whether the current user's granted authorities include `ROLE_ADMIN`. Non-admin users receive HTTP `403 Forbidden`.

#### Q33: Why is CSRF (Cross-Site Request Forgery) disabled in `SecurityConfig`?
**Answer**: CSRF attacks exploit web browsers that automatically attach ambient credentials (session cookies) to cross-origin requests. Since this REST API uses stateless JWT Bearer tokens passed explicitly in the `Authorization` header (which browsers do not attach automatically), CSRF protection is unnecessary.

#### Q34: What happens if an attacker tampers with a JWT payload?
**Answer**: Any modification to the payload alters its cryptographic hash. When `JwtTokenProvider` validates the token using the server's private secret key, the calculated signature does not match the signature in the token, throwing a `SignatureException` and rejecting the request.

#### Q35: What is CORS and how is it configured in Spring Security?
**Answer**: Cross-Origin Resource Sharing (CORS) is a browser security mechanism that restricts web applications from making HTTP requests to a different domain/port. In `SecurityConfig`, a `CorsConfigurationSource` bean explicitly declares permitted origins (`*` or specific UI domains), HTTP methods (GET, POST, PUT, DELETE), and allowed headers.

---

## Section 4: Testing Pyramid, JUnit 5 & Mockito

#### Q36: Explain the 5 levels of the Test Pyramid implemented in this project.
**Answer**:
1. **Level 1 (Unit)**: Tests business logic in isolation using JUnit 5 and Mockito mocks.
2. **Level 2 (Repository)**: Tests JPA entity mapping, constraints, and custom queries against H2.
3. **Level 3 (Consistency & FSM)**: Tests deterministic state transitions and priority recalculation.
4. **Level 4 (API / Controller)**: Tests HTTP endpoints, JSON serialization, and security slices via MockMvc.
5. **Level 5 (End-to-End & Frontend)**: Tests complete multi-step user workflows and HTML page availability.

#### Q37: What is Mockito and what is the difference between `@Mock` and `@InjectMocks`?
**Answer**: Mockito is a Java mocking library. `@Mock` creates a mock implementation of a collaborating class where method calls can be stubbed. `@InjectMocks` creates a real instance of the class under test and automatically injects all annotated `@Mock` fields into its constructor or properties.

#### Q38: What does `MockMvc` do in Spring Boot test suites?
**Answer**: `MockMvc` is a testing framework that simulates HTTP requests and assertions directly through Spring's `DispatcherServlet` without requiring a running network web server, enabling fast, isolated controller testing.

#### Q39: What is the difference between `@SpringBootTest` and `@WebMvcTest`?
**Answer**: `@SpringBootTest` boots the full Spring application context, including all beans, repositories, and configurations. `@WebMvcTest` is a sliced test that boots only the web tier (controllers, filters, advice), mocking dependencies to provide fast, targeted unit testing of HTTP APIs.

#### Q40: What is the purpose of `@ActiveProfiles("test")` in test classes?
**Answer**: It instructs Spring to load properties from `src/test/resources/application.properties` (which configures the H2 in-memory database and disables external services), ensuring production and development properties are not used during test execution.

#### Q41: Why is `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` used in `PrintQueueE2ETest`?
**Answer**: It enforces deterministic sequential execution of end-to-end steps (e.g. `testUserRegistration` at `@Order(1)` must run before `testUserLogin` at `@Order(2)` and `testSubmitJob` at `@Order(3)`).

#### Q42: What is the difference between JUnit 4 and JUnit 5?
**Answer**: JUnit 5 is built on a modular architecture (`JUnit Platform`, `JUnit Jupiter`, `JUnit Vintage`). It supports Java 8+ features (lambdas, streams), parameter injection, nested tests (`@Nested`), conditional execution (`@EnabledOnOs`), and uses new annotations (`@BeforeEach` instead of `@Before`, `@Test` from package `org.junit.jupiter.api`).

#### Q43: How do we assert that an exception is thrown in JUnit 5?
**Answer**: Using `assertThrows()`:
```java
assertThrows(BadRequestException.class, () -> userService.register(duplicateRequest));
```

#### Q44: What is the role of Maven Surefire plugin?
**Answer**: `maven-surefire-plugin` executes automated unit and integration tests during the Maven `test` phase. It collects test results and outputs XML and plain text reports in `target/surefire-reports/` consumed by Jenkins CI pipelines.

#### Q45: What is regression testing and why is it critical in CI/CD?
**Answer**: Regression testing runs existing test suites after every code change to confirm that new features or bug fixes have not broken existing, verified functionality.

#### Q46: How does `FrontendPagesIntegrationTest` verify the user interface?
**Answer**: It issues HTTP `GET` requests to all 13 HTML page endpoints (e.g. `/`, `/login.html`, `/dashboard.html`, `/printer.html`) via MockMvc and asserts that each returns HTTP `200 OK` with `text/html` content type.

#### Q47: What is code coverage and which tool is standard in Maven for measuring it?
**Answer**: Code coverage measures the percentage of source code executed during automated tests (line, branch, and instruction coverage). **JaCoCo** (`jacoco-maven-plugin`) is the standard tool used to generate coverage reports and enforce coverage build thresholds.

#### Q48: Why are empty or stubbed tests forbidden in this project?
**Answer**: Empty or always-passing tests create false confidence, masking critical software defects, invalidating quality gate guarantees, and undermining automated pipeline integrity.

---

## Section 5: Version Control Systems & Git / GitHub

#### Q49: What is Git and how does it differ from centralized VCS like SVN?
**Answer**: Git is a distributed version control system. Every developer maintains a full local clone of the repository history. Centralized VCS relies on a single central server; if the server fails, developers cannot commit, branch, or view history.

#### Q50: Explain Git's internal three-tree architecture.
**Answer**:
1. **Working Directory**: The local filesystem sandbox containing checked-out files.
2. **Staging Area (Index)**: A staging intermediate file tracking changes prepared for the next commit.
3. **Commit History (Repository / HEAD)**: The permanent directed acyclic graph (DAG) of cryptographically signed immutable commit snapshots.

#### Q51: What does `git init` do?
**Answer**: It creates an empty Git repository or reinitializes an existing one by generating the hidden `.git/` directory containing configuration, object databases, and ref pointers.

#### Q52: What is the purpose of `.gitignore`?
**Answer**: It specifies file patterns that Git should intentionally ignore and never track, such as build outputs (`target/`), IDE settings (`.idea/`, `.vscode/`), and sensitive environment files (`.env`).

#### Q53: What is the difference between `git switch` and `git checkout`?
**Answer**: In modern Git, `git switch` is dedicated exclusively to switching and creating branches (`git switch -c <name>`). `git checkout` is a legacy multi-purpose command used for switching branches, checking out commits, and restoring working tree files.

#### Q54: What is a detached HEAD state in Git?
**Answer**: It occurs when HEAD points directly to a specific commit hash rather than a named branch reference. Commits made in this state become orphaned when switching branches unless a branch is explicitly created pointing to them.

#### Q55: What is a Fast-Forward merge?
**Answer**: A merge where the current branch head has no divergent commits relative to the branch being merged. Git simply moves the target branch pointer forward to the latest commit without creating a new merge commit.

#### Q56: How does a merge conflict occur and how is it resolved?
**Answer**: A conflict occurs when two branches modify the exact same lines of a file and Git cannot determine which version takes precedence. Git pauses the merge and embeds conflict markers:
```
<<<<<<< HEAD
Our version
=======
Their version
>>>>>>> feature-branch
```
The developer manually edits the file to resolve the conflict, stages it (`git add`), and commits (`git commit`).

#### Q57: What is the difference between `git rebase` and `git merge`?
**Answer**:
- `git merge`: Combines two branches by creating a new merge commit, preserving historical chronology and branch topology.
- `git rebase`: Moves the entire feature branch to begin from the tip of the base branch, rewriting commit hashes to produce a clean, linear commit history.

#### Q58: What is a Git hook?
**Answer**: A script that Git executes automatically before or after key actions (e.g. `pre-commit`, `commit-msg`, `pre-push`). In DevOps, client-side hooks format code or run linter checks, while server-side hooks validate commit messages or trigger CI builds.

#### Q59: What does `git log --oneline --graph --decorate` do?
**Answer**:
- `--oneline`: Condenses each commit to its short SHA and subject line.
- `--graph`: Renders an ASCII character graph of branch topologies.
- `--decorate`: Displays ref names (branch heads, tags, HEAD).

#### Q60: What is the difference between `git reset` and `git revert`?
**Answer**:
- `git reset`: Moves the branch pointer backward, altering history (safe only on private local branches).
- `git revert`: Creates a brand-new commit that applies the inverse of an earlier commit, safely undoing changes without rewriting shared public history.

---

## Section 6: Continuous Integration & Jenkins CI/CD

#### Q61: What is Continuous Integration (CI)?
**Answer**: CI is a DevOps software development practice where developers frequently merge code changes into a shared repository branch. Automated builds and test suites execute on each push to detect integration errors as early as possible.

#### Q62: What is Continuous Delivery (CD) vs Continuous Deployment?
**Answer**:
- **Continuous Delivery**: The pipeline automatically builds, tests, and stages deployable release artifacts; production deployment requires a manual approval trigger.
- **Continuous Deployment**: Every code change that passes all pipeline automated tests and quality gates is deployed to production automatically without manual intervention.

#### Q63: What is a Jenkinsfile and why is "Pipeline-as-Code" beneficial?
**Answer**: A `Jenkinsfile` is a text file containing the definition of a Jenkins pipeline written in Groovy syntax. "Pipeline-as-Code" allows pipeline definitions to be version-controlled, tracked, reviewed via pull requests, and branched alongside application code.

#### Q64: What is the structure of a Declarative Jenkins Pipeline?
**Answer**:
```groovy
pipeline {
    agent any
    environment { ... }
    stages {
        stage('Stage Name') {
            steps { ... }
        }
    }
    post {
        always { ... }
        success { ... }
        failure { ... }
    }
}
```

#### Q65: What is the purpose of the `post` block in a Jenkinsfile?
**Answer**: It defines actions to execute after all stages complete based on build status:
- `always`: Runs regardless of build outcome (e.g. publishing JUnit reports, cleaning workspaces).
- `success`: Runs only if the build succeeded.
- `failure`: Runs only if any stage failed (e.g. sending alerts).

#### Q66: How does Jenkins consume and display Maven test results?
**Answer**: Using the `junit` step:
```groovy
junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
```
Jenkins parses the JUnit XML reports, generates visual test trend charts, displays test counts, and marks builds as UNSTABLE or FAILED if tests fail.

#### Q67: What is a Quality Gate in Jenkins and how is it implemented in this project?
**Answer**: A Quality Gate is an automated enforcement checkpoint. In `Jenkinsfile`, Stage 7 (`Quality Gate`) inspects Surefire test results; if any of the 120 tests failed or skipped unexpectedly, the pipeline terminates immediately, blocking packaging, Docker build, and Ansible deployment.

#### Q68: What is a Jenkins Agent / Node?
**Answer**: An agent (or worker node) is a machine or container configured to execute jobs dispatched by the Jenkins controller. This offloads resource-heavy compilation and container builds from the master node.

#### Q69: How do GitHub Webhooks trigger Jenkins builds?
**Answer**: On a `git push`, GitHub dispatches an HTTP POST request with commit metadata to the Jenkins webhook listener endpoint (`/github-webhook/`). Jenkins validates the repository URL and automatically triggers a build on the corresponding pipeline.

#### Q70: Why can't GitHub directly deliver webhooks to `http://localhost:8080`?
**Answer**: `localhost` refers to the loopback network interface accessible only within the local machine. Because it is not a publicly routable IP address, public cloud services like GitHub cannot route traffic to it.

#### Q71: How is the localhost limitation overcome in student lab environments?
**Answer**: By using secure tunneling tools (e.g. **Ngrok**, **Cloudflare Tunnels**, **LocalTunnel**) that establish an encrypted outbound tunnel from the local machine to a public cloud proxy, providing a publicly accessible HTTPS URL. Alternatively, SCM Polling (`pollSCM`) can be used.

#### Q72: What is the difference between `archiveArtifacts` and publishing a report in Jenkins?
**Answer**: `archiveArtifacts` stores binary deliverables (like `digital-print-queue-1.0.0.jar`) on the Jenkins controller so they can be downloaded or deployed later. Report publishing (like JUnit) parses diagnostic data to display interactive HTML charts and test metrics.

---

## Section 7: Containerization & Docker

#### Q73: What is Docker and how does containerization differ from traditional Virtual Machines?
**Answer**: Containers package an application and its dependencies into a lightweight process sharing the host operating system's kernel, utilizing Linux cgroups and namespaces. Virtual Machines run a full guest OS on top of a hypervisor, consuming significantly more CPU, disk, and memory overhead.

#### Q74: Explain the difference between a Docker Image and a Docker Container.
**Answer**:
- **Docker Image**: An immutable, read-only template containing the application code, libraries, and runtime environment, structured as stacked layers.
- **Docker Container**: A runnable, isolated runtime instance of an image with a thin, writable top layer.

#### Q75: Explain the structure of the multi-stage `Dockerfile` in this project.
**Answer**:
- **Stage 1 (`builder`)**: Uses `maven:3.9.6-eclipse-temurin-21-jammy` to resolve dependencies, compile code, and package the JAR.
- **Stage 2 (`runtime`)**: Uses lightweight `eclipse-temurin:21-jre-jammy`, copies only the compiled JAR from the builder stage, configures a non-root user, and runs the application.

#### Q76: Why are multi-stage builds considered a DevOps best practice?
**Answer**:
1. **Size Optimization**: Leaves heavy build tools (Maven, JDK, source files) behind, producing minimal images (~185–360MB instead of >800MB).
2. **Security**: Eliminates compilers and build tooling from the runtime environment, drastically reducing the attack surface.

#### Q77: Why does our container run as user `appuser` (UID 10001) instead of `root`?
**Answer**: Running as root inside a container presents a serious security vulnerability. If an attacker exploits an application vulnerability to achieve container breakout, they would gain root privileges on the host operating system. A non-root UID restricts privileges to the container's isolated context.

#### Q78: What is `.dockerignore` and why is it important?
**Answer**: Similar to `.gitignore`, it excludes files and directories (such as `target/`, `.git/`, `.env`) from the Docker build context sent to the Docker daemon, speeding up image builds and preventing sensitive files from being baked into layers.

#### Q79: What is Docker Compose?
**Answer**: Docker Compose is a tool for defining and running multi-container Docker applications using a declarative YAML file (`docker-compose.yml`), managing service configurations, dependencies, networks, and persistent storage volumes with single commands (`docker-compose up`).

#### Q80: What is the difference between a Docker Volume and a Bind Mount?
**Answer**:
- **Docker Volume**: Managed exclusively by Docker inside `/var/lib/docker/volumes/`. Platform-independent, isolated, and safe from unintended host modifications.
- **Bind Mount**: Directly maps a specific host directory or file to a container path. Dependent on host filesystem structure.

#### Q81: What is the purpose of the named volume `printqueue_uploads` in this project?
**Answer**: It mounts persistent storage to `/app/uploads`, ensuring uploaded documents and generated print files survive application container restarts, updates, and recreation.

#### Q82: How does Docker Compose guarantee that the application doesn't start before MySQL is ready?
**Answer**: By defining a `healthcheck` on the MySQL service (using `mysqladmin ping`) and configuring the application service with `depends_on: { mysql: { condition: service_healthy } }`.

#### Q83: In the base-image comparison experiment, what were the trade-offs between Alpine and Ubuntu?
**Answer**:
- **Alpine Linux**: Ultra-compact (~185MB), minimal vulnerability footprint, uses `musl libc`.
- **Ubuntu Jammy**: Larger (~360MB), uses standard `glibc`, offering broader binary compatibility with native libraries.

#### Q84: What does `docker exec -it <container> /bin/bash` do?
**Answer**: It opens an interactive terminal (`-i` interactive, `-t` TTY pseudo-terminal) inside a running container, allowing administrators to inspect processes, environment variables, and files directly.

#### Q85: What does Docker port mapping `-p 8080:8080` signify?
**Answer**: It binds port 8080 on the host machine to port 8080 inside the container. Network requests hitting host port 8080 are routed through Docker's bridge network to the container's listening port.

---

## Section 8: Configuration Management & Ansible

#### Q86: What is Configuration Management?
**Answer**: The automated practice of establishing and maintaining software systems, server configurations, dependencies, and environments in a known, desired, and consistent state over time.

#### Q87: What is Ansible and how does its architecture differ from Chef and Puppet?
**Answer**: Ansible is an open-source, agentless configuration management tool. While Chef and Puppet require heavy client agent daemons running on every managed node, Ansible is completely agentless, connecting over standard SSH (Linux) or WinRM (Windows) and executing temporary Python modules.

#### Q88: What is Ansible Inventory?
**Answer**: A file (such as `ansible/inventory.ini`) listing target managed nodes, hostnames, IP addresses, connection credentials, and logical host groups (e.g. `[appservers]`, `[webservers]`, `[dbservers]`).

#### Q89: What is an Ansible Playbook?
**Answer**: A declarative YAML document that defines the desired configuration and tasks to execute on specific host groups, orchestrating modules in sequential order.

#### Q90: What is **Idempotency** in Ansible and why is it essential?
**Answer**: An operation is idempotent if executing it multiple times produces the exact same system state without making unintended duplicate changes. If a package is already installed or a file is already present with the desired content, Ansible recognizes the state and reports `ok` (green) instead of `changed` (yellow).

#### Q91: What is the role of Nginx as configured by `ansible/webserver.yml`?
**Answer**: Nginx acts as an edge reverse proxy web server listening on external standard port 80. It terminates HTTP connections, handles client request limits (20MB upload size), and reverse-proxies requests internally to the Spring Boot application running on port 8080.

#### Q92: What are Ansible Handlers and when do they execute?
**Answer**: Handlers are special tasks that execute only when notified by another task that reported a `changed` status. They run once at the end of the play (e.g. reloading the Nginx service only when its configuration file has been modified).

#### Q93: What are Jinja2 templates in Ansible?
**Answer**: Jinja2 templates (`.j2` files) are dynamically rendered configuration files containing variables and conditionals (e.g. `{{ app_port }}`) evaluated at runtime before being deployed to managed nodes.

#### Q94: How does Ansible manage Docker containers?
**Answer**: Using Ansible's `community.docker` collection (`docker_container` and `docker_image` modules), Ansible can build images, manage container lifecycles, configure port mappings, and attach persistent volumes declaratively in playbooks.

#### Q95: How do we verify Ansible playbooks before executing them in production?
**Answer**:
1. Syntax validation: `ansible-playbook --syntax-check site.yml`
2. Dry-run execution: `ansible-playbook --check site.yml` (simulates changes without modifying the target system).

---

## Section 9: DevOps Principles, SRE & System Design

#### Q96: Define DevOps in your own words.
**Answer**: DevOps is a cultural and technical methodology that bridges software development (Dev) and IT operations (Ops) through automated workflows, shared responsibility, continuous integration and delivery, infrastructure as code, and iterative feedback loops to deliver reliable software faster.

#### Q97: What are the DORA (DevOps Research and Assessment) metrics?
**Answer**:
1. **Deployment Frequency**: How often code is successfully deployed to production.
2. **Lead Time for Changes**: Time elapsed from code commit to running in production.
3. **Change Failure Rate**: Percentage of deployments requiring hotfixes, patches, or rollbacks.
4. **Mean Time to Recovery (MTTR)**: Time required to restore service after an incident.

#### Q98: What is Infrastructure as Code (IaC)?
**Answer**: The practice of managing and provisioning computing infrastructure (servers, networks, storage, containers) through declarative, machine-readable configuration files (e.g. Dockerfiles, Compose YAML, Ansible playbooks) rather than manual physical processes or interactive web consoles.

#### Q99: What is "Shift-Left" in modern DevOps?
**Answer**: Shift-Left refers to moving testing, security vulnerability scanning, and quality assurance earlier in the software development lifecycle (towards the left of the timeline), ensuring defects are identified and remediated during unit/build phases before code merges into main branches.

#### Q100: How does this project handle database connection pooling?
**Answer**: Via **HikariCP**, the default high-performance JDBC connection pool in Spring Boot. It pre-allocates and maintains reusable database connections, minimizing connection establishment latency and efficiently managing database connection limits under high concurrent load.

#### Q101: What is the difference between Blue-Green Deployment and Rolling Deployment?
**Answer**:
- **Blue-Green**: Maintains two identical production environments (Blue and Green). One serves live traffic while the other is updated. Traffic is instantly switched via router/proxy. Provides instant zero-downtime cutover and immediate rollback.
- **Rolling**: Gradually replaces instances of previous versions with the new version one node or pod at a time until all instances are running the updated software.

#### Q102: How does the priority queue scheduling algorithm handle starvation in print jobs?
**Answer**: While `HIGH` priority jobs are placed ahead of `NORMAL` and `LOW` priority jobs, jobs within the same priority tier are strictly scheduled using **FIFO (First-In, First-Out)** based on their `submittedAt` timestamp. To completely prevent starvation in large systems, an aging mechanism can progressively elevate the priority of low-priority jobs that have remained queued beyond a threshold time.

#### Q103: What is the single source of truth in this project?
**Answer**: **`PROJECT_CONTRACT.md`** serves as the immutable architectural specification defining entities, API contracts, business rules, database schema, and operational constraints across all phases.

#### Q104: What is the significance of the 100% Quality Gate pass rate?
**Answer**: It ensures that no breaking changes, regression bugs, security regressions, or broken contracts can reach the deployment stage. If even a single test fails, deployment is automatically prevented by the CI/CD pipeline.

#### Q105: What is the difference between Monolithic and Microservices architecture?
**Answer**:
- **Monolithic**: Single unified deployable unit containing all application tiers and business modules. Simple to develop and test, but difficult to scale independently.
- **Microservices**: Decomposes the application into loosely coupled, independently deployable services organized around business capabilities, communicating via lightweight protocols (REST, gRPC, messaging). Offers fine-grained horizontal scaling at the cost of operational and network complexity.
