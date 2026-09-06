# Pay-as-you-Go Personal Cloud — Full Project Analysis (Updated)

> **Context**: Canonical Graduate Software Engineer interview. This analysis covers the backend (Spring Boot 3.5 / Java 21) and frontend (React 19 / CRA), identifies architectural issues, OOP/best-practice violations, and provides design-decision tradeoffs you should be able to articulate.
> 
> *Note: Tier 1 critical bugs, Tier 2 architectural refactoring (domain packaging, service interfaces, unit tests), and Tier 3 DevOps tasks (Docker, Swagger, README) have already been implemented. The primary remaining technical debt lies in the frontend.*

---

## 1. Project Overview & What's Good

Your project is a **personal cloud storage platform** with AWS S3-style pay-as-you-go billing. This is a genuinely interesting project idea that demonstrates:

- ✅ **Full-stack ownership** — Spring Boot REST API + React SPA
- ✅ **Real cloud integration** — AWS S3 SDK for object storage
- ✅ **Security** — JWT auth with Spring Security, BCrypt password hashing, stateless sessions
- ✅ **Domain modelling** — User, FileMetadata, UserUsage entities with proper JPA relationships
- ✅ **Usage-based billing** — Tracking per-operation costs (PUT, GET, LIST, etc.) mirroring real S3 pricing
- ✅ **Structured logging** — Custom `AppLogger` wrapper + Logback file appender
- ✅ **Global exception handling** — `@ControllerAdvice` with structured `ApiError` responses
- ✅ **Constructor injection** everywhere (no `@Autowired` on fields — this is correct)

---

## 2. Architecture & OOP Analysis (Priority to fix)

### 🔴 Flat Package Structure — No Layering

**Current state**: All 25 Java classes live in a single flat package:
```
com.btech_major_project.Personal_Cloud/
  ├── AuthController.java
  ├── AuthService.java
  ├── ... (everything in one package)
  └── dto/
```

**Problem**: Violates **Separation of Concerns** and makes the codebase hard to navigate. An interviewer at Canonical (who maintain enterprise-grade systems) will expect proper layering.

**Recommended package structure:**
```
com.personalcloud/
  ├── PersonalCloudApplication.java
  ├── config/          ← SecurityConfig, AwsConfig, WebConfig
  ├── security/        ← JwtService, JwtFilter, EntryPoint, AccessDeniedHandler
  ├── auth/            ← AuthController, AuthService
  ├── storage/         ← FileController, StorageService, FileMetadata, FileMetadataRepository
  ├── billing/         ← BillingController, BillingService
  ├── usage/           ← UsageService, UserUsage, UserUsageRepository
  ├── user/            ← User, UserRepository, AppUserDetailsService
  ├── common/          ← AppLogger
  └── dto/             ← All DTOs
```

**What to say in interview**: "I restructured from a flat package into a domain-driven package layout. This follows the **Package by Feature** principle, improving cohesion — each package contains everything related to one domain concept. The alternative is **Package by Layer** (controllers/, services/, repositories/) which is simpler but leads to shotgun surgery when modifying a feature."

---

### 🔴 Missing Interfaces for Services (OOP Violation)

None of your services implement interfaces:

```java
// Current
@Service
public class StorageService { ... }

// Should be
public interface StorageService {
    FileMetadata upload(User user, MultipartFile file, String subPath) throws IOException;
    List<FileMetadata> list(User user);
    ResponseEntity<InputStreamResource> download(User user, Long fileId);
    void delete(User user, Long fileId);
}

@Service
public class S3StorageService implements StorageService { ... }
```

**Why this matters (tradeoffs to articulate):**

| Approach | Pros | Cons |
|----------|------|------|
| **Concrete class only** | Simpler, fewer files, YAGNI | Can't swap implementations, harder to mock in tests, violates Dependency Inversion Principle |
| **Interface + Implementation** | Enables DI properly, testable with mocks, can swap to GCS/MinIO/local FS, follows SOLID | More boilerplate, overkill for single-impl services |

**What to say**: "For a cloud storage platform, the storage backend is the most natural place for an interface — we might want to swap from S3 to GCS or to a local filesystem for testing. `BillingService` could also benefit from an interface if we want to support different pricing models."

---

### 🔴 Controller-Repository Coupling

All three controllers inject `UserRepository` directly to resolve the current user:

```java
// AuthController, FileController, BillingController all have:
private final UserRepository userRepository;
// ...
User user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
```

**Problems:**
1. Duplicated user-resolution logic across 3 controllers
2. Controllers directly depend on the repository layer (should go through a service)
3. Violates **DRY** and **Single Responsibility**

**Fix**: Create a `UserService` or use a custom `@AuthenticationPrincipal` resolver that injects the `User` entity directly into controller methods.

---

### 🟡 `BillingSummary` Nested Inside `BillingService` (Resolved ✅)

[BillingService.java](file:///Users/vaibhavgolhar/Downloads/Pay-as-you-Go%20Personal%20Cloud/aws-personal-cloud-backend/src/main/java/com/btech_major_project/Personal_Cloud/billing/StandardBillingService.java) — `BillingSummary` was defined as a `public static` inner class of `BillingService`.

**Tradeoff:**
- Inner class keeps it co-located (convenient for small projects)
- But it's used by `BillingController` as a response DTO — it belongs in the `dto` package for consistency
- Makes the class name awkward: `BillingService.BillingSummary`

---

### 🟡 `StorageService.download()` Returns `ResponseEntity` (Resolved ✅)

[StorageService.java](file:///Users/vaibhavgolhar/Downloads/Pay-as-you-Go%20Personal%20Cloud/aws-personal-cloud-backend/src/main/java/com/btech_major_project/Personal_Cloud/storage/StorageService.java) — A service method used to return `ResponseEntity<InputStreamResource>`. This **couples the service layer to HTTP concerns**.

**Better approach**: Return a domain object (e.g., `FileDownloadResult` with `InputStream`, `contentType`, `filename`) and let the controller build the `ResponseEntity`.

---

## 3. Security Issues

### 🔴 Admin Check by Username String

In [App.js](file:///Users/vaibhavgolhar/Downloads/Pay-as-you-Go%20Personal%20Cloud/aws-personal-cloud-frontend/src/App.js):
```javascript
const isAdmin = user?.username === "admin";
```

The admin check is **only on the frontend**. The backend has no admin role, no `ROLE_ADMIN`, and no admin endpoints. The frontend references `/api/admin/*` endpoints that **don't exist** in the backend. This is:
1. A security vulnerability (frontend-only authorization)
2. Incomplete feature (backend admin endpoints are missing)

**Fix**: Add a `role` field to `User` entity, enforce `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints.

---

### 🔴 JWT Secret from Environment — No Validation

```properties
app.jwt.secret=${JWT_SECRET}
```

If `JWT_SECRET` is not set, the app will fail at runtime with an unclear error. Add `@NotBlank` validation or a startup check.

Also: The secret should be at least 256 bits (32 bytes) for HMAC-SHA256. There's no minimum length validation.

---

### 🟡 No CORS Configuration

`SecurityConfig` uses `cors(Customizer.withDefaults())` which enables CORS with defaults (all origins allowed for simple requests). In production, you need a `CorsConfigurationSource` bean that whitelists specific origins.

---

## 4. Frontend Architecture Issues

### 🔴 Single 900-Line God Component (Resolved ✅)

[App.js](file:///Users/vaibhavgolhar/Downloads/Pay-as-you-Go%20Personal%20Cloud/aws-personal-cloud-frontend/src/App.js) was previously nearly **900 lines** with 40+ `useState` hooks. It has now been successfully refactored into smaller components under `src/components/` following the Single Responsibility Principle.

**What to say in interview**: "I would decompose this into components following the **Single Responsibility Principle**:
- `AuthPage` — login/register form
- `Layout` — header, sidebar, routing
- `FileExplorer` — folder navigation, file list, upload
- `BillingDashboard` — billing summary display
- `ProfilePage` — user profile
- `AdminPanel` — admin features

With a **custom hook** for auth state (`useAuth`) and file operations (`useFiles`)."

---

## 5. Data Integrity & Transactional Issues

### 🔴 Non-Atomic S3 + DB Operations

In [S3StorageService.upload()](file:///Users/vaibhavgolhar/Downloads/Pay-as-you-Go%20Personal%20Cloud/aws-personal-cloud-backend/src/main/java/com/btech_major_project/Personal_Cloud/storage/S3StorageService.java):

```java
// 1. Upload to S3
s3.putObject(putReq, RequestBody.fromBytes(file.getBytes()));
// 2. Save metadata to DB
fileRepo.save(meta);
// 3. Update usage counters
usageService.onPut(user, saved.getSizeBytes(), true);
```

If step 2 or 3 fails, you have an **orphaned object in S3** with no DB record. Similarly, `delete()` deletes from S3 first, then DB — if the DB delete fails, you have a DB record pointing to a deleted S3 object.

**Tradeoff to articulate:**
- **Approach 1 (current)**: Simple, fast, but not transactional across S3+DB
- **Approach 2 (Outbox pattern)**: Write to DB first (with status "PENDING"), then upload to S3, then update status to "ACTIVE". A background job cleans up PENDING records
- **Approach 3 (Saga pattern)**: Compensating transactions — if DB save fails, delete from S3
- **What to say**: "For a production system, I'd implement the **Outbox pattern** — record the intent in the database first within a transaction, then perform the S3 operation, then update the status. A periodic cleanup job handles any inconsistencies."

---

## 6. Missing Features That Interviewers Will Ask About

| Feature | Why They'll Ask | Quick Answer |
|---------|----------------|--------------|
| **Unit tests** | Only 1 empty test file. Canonical values TDD. | "I would add tests for `BillingService.calculateCurrent()`, `JwtService`, and `StorageService` using Mockito for S3 mocking" |
| **API documentation** | No Swagger/OpenAPI | "I'd add `springdoc-openapi-starter-webmvc-ui` for auto-generated API docs" |
| **Pagination** | File listing returns ALL files | "I'd use Spring Data's `Pageable` — `findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable)` returning `Page<FileMetadata>`" |
| **File size limits per user** | No quota enforcement | "The `UserUsage.totalBytesStored` is tracked but never checked. I'd add a configurable quota with a check before upload" |
| **Docker / docker-compose** | No containerization | "For Canonical especially, I'd add a `Dockerfile` (multi-stage build) and `docker-compose.yml` with PostgreSQL + app services" |
| **README** | Backend has no README at all | "A good README with architecture diagram, setup instructions, API documentation" |

---

## 7. Design Decisions & Tradeoff Matrix

These are questions an interviewer **will** ask. Be prepared to defend each choice.

### Why Spring Boot over alternatives?

| Option | Pros | Cons | Why We Chose It |
|--------|------|------|-----------------|
| **Spring Boot** ✅ | Mature ecosystem, Spring Security, JPA, massive community, enterprise-standard | Heavy, slow startup, opinionated | Best for demonstrating production-grade patterns |
| **Quarkus** | Fast startup, GraalVM native, modern | Smaller ecosystem, less hiring familiarity | Good alternative for cloud-native |
| **Node.js/Express** | Fast development, same language as frontend | Weaker type safety, less structured | Better for prototypes |
| **Django** | Batteries-included, admin panel, ORM | Python, less common for cloud services | Good for MVPs |

### Why PostgreSQL over alternatives?

| Option | Pros | Cons | Why We Chose It |
|--------|------|------|-----------------|
| **PostgreSQL** ✅ | ACID, relational integrity, JSON support, free | Needs management, schema migrations | Best fit for structured user/file/usage data |
| **MongoDB** | Schema-flexible, easy to start | No ACID by default, harder joins | File metadata is inherently relational |
| **DynamoDB** | Serverless, scales infinitely | Vendor lock-in, limited queries | Would couple entirely to AWS |

### Why JWT over Session-based Auth?

| Option | Pros | Cons | Why We Chose It |
|--------|------|------|-----------------|
| **JWT (stateless)** ✅ | No server-side session store, scales horizontally, works with SPA | Can't revoke tokens, larger payload, must handle refresh | Fits stateless REST + React SPA architecture |
| **Session cookies** | Simple, revocable, smaller | Needs session store (Redis), CSRF protection | Better for server-rendered apps |

### Why S3 over alternatives for object storage?

| Option | Pros | Cons | Why We Chose It |
|--------|------|------|-----------------|
| **AWS S3** ✅ | Industry standard, 11 nines durability, pre-signed URLs | Vendor lock-in, cost | The project's billing model mirrors S3 pricing |
| **MinIO** | S3-compatible, self-hosted, open source | Must manage infrastructure | Great for local dev/testing |

### Why React (CRA) over alternatives?

| Option | Pros | Cons | Why We Chose It |
|--------|------|------|-----------------|
| **React + CRA** ✅ | Simple setup, widely known | CRA is deprecated/unmaintained | Quick start for SPA |
| **React + Vite** | Fast HMR, modern tooling | Slightly more config | **Better choice** — CRA is officially deprecated |

> [!WARNING]
> **CRA is deprecated**. Facebook/Meta officially recommends using Vite or Next.js. You should mention this awareness in the interview: "I started with CRA for rapid prototyping but would migrate to Vite for a production project."

---

## 8. Prioritised Improvement Roadmap

### Tier 1 — COMPLETED ✅
- Deleted `S3Config.java` and `S3Bucket.java` (dead code)
- Fixed `pom.xml` Java version mismatch
- Replaced anonymous `Object` in `/api/auth/me` with proper DTO
- Fixed `handleDownload()` to use `fetch()` + blob instead of `window.open()`
- Removed `AWSCLIV2.pkg` from repo, updated `.gitignore`
- Deleted duplicate `PayAsYouGoCloudApp.jsx`

### Tier 2 — High-Impact Improvements (Architecture & OOP) — COMPLETED ✅

| # | Change | Impact | Status |
|---|--------|--------|--------|
| 7 | Restructure into domain-based packages | Shows architectural thinking | ✅ Done |
| 8 | Extract interfaces for `StorageService` and `BillingService` | OOP, testability | ✅ Done |
| 9 | Create `UserService` to eliminate controller→repository coupling | DRY, SRP | ✅ Done |
| 10 | Add unit tests for `BillingService`, `JwtService`, `StorageService` | Shows testing discipline | ✅ Done |
| 11 | Move `BillingSummary` to `dto/` package | Consistency | ✅ Done |
| 12 | Fix `StorageService.download()` to not return `ResponseEntity` | Layer separation | ✅ Done |

### Tier 3 — Professional Polish

| # | Change | Impact | Status |
|---|--------|--------|--------|
| 13 | Split `App.js` into components + custom hooks | React best practices | ✅ Done |
| 14 | Add Spring profiles (`dev`, `prod`) | Production readiness | Pending |
| 15 | Add `Dockerfile` + `docker-compose.yml` | DevOps awareness | ✅ Done |
| 16 | Add Swagger/OpenAPI documentation | API professionalism | ✅ Done |
| 17 | Add CORS whitelist configuration | Security | Pending |
| 18 | Replace `AppLogger` with direct SLF4J + parameterised logging | Performance, convention | Pending |
| 19 | Use environment variables for frontend API URL | Deployment readiness | Pending |
| 20 | Add a comprehensive `README.md` | First impression | ✅ Done |
