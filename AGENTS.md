# AGENTS.md

## Project Overview

Mura Dashboard is a flaky test tracking and visualization application. It accepts test report submissions via a REST API, detects flaky tests (tests that both fail and succeed across retries within the same report), stores results in PostgreSQL, and serves a React dashboard for browsing, filtering, and paginating flaky test data.

## Architecture

- **Monorepo:** backend (`mura-dashboard/`), frontend (`mura-dashboard/frontend/`), Helm chart (`mura-dashboard-helm-chart/`)
- **Backend:** Java 25, Spring Boot 4.0.3 (MVC), JPA/Hibernate, Flyway migrations, PostgreSQL 18
- **Frontend:** TypeScript ~5.9.3 (strict), React 19.1, MUI 7, Vite 7.3.1 — built into `static/` and served by Spring Boot
- **Two API tiers:**
  - `/api/**` — backend endpoints, secured with HTTP Basic auth
  - `/rapi/**` — frontend endpoints, public (no auth)
- **No Dockerfile** — Docker images are built via Spring Boot `bootBuildImage` (Paketo Buildpacks)

## Build & Run

```bash
# Full build (backend + frontend, runs tests — needs Docker for Testcontainers)
cd mura-dashboard
./gradlew build

# Run locally (starts Spring Boot on :8080, uses docker-compose.yaml for PostgreSQL)
./gradlew bootRun

# Frontend dev server (proxies /rapi and /login to localhost:8080)
cd mura-dashboard/frontend
npm install
npm run dev

# Build Docker image
cd mura-dashboard
./gradlew bootBuildImage

# Local stack via Docker Compose
cd mura-dashboard
docker compose up
```

**Prerequisites:** JDK 25, Docker (for Testcontainers and PostgreSQL). Node 22.15.0 is auto-downloaded by the Gradle Node plugin.

## Testing

```bash
cd mura-dashboard
./gradlew check        # runs all tests
./gradlew test         # unit + integration tests only
```

- **Frameworks:** JUnit 5, Spring Boot Test (`@SpringBootTest` + `@AutoConfigureMockMvc`), MockMvc, Spring REST Docs, Testcontainers (PostgreSQL 18)
- **Docker required:** Testcontainers starts a real PostgreSQL instance — Docker must be running
- **Test data seeder:** `TestDataConfiguration` + `TestMuraDashboardApplication` for local dev with sample data
- **No frontend tests** are configured

## Code Conventions — Java

- **Records** for all DTOs (`*Request`, `*Response`, `*Summary`)
- **Lombok** on entities and services (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`)
- **Constructor injection** — explicit constructors or `@RequiredArgsConstructor`
- **Naming suffixes:** `*Entity`, `*Repository`, `*Service`, `*Controller`, `*Request`, `*Response`
- **OpenAPI annotations** on controllers and DTOs (`@Schema`, `@Operation`, `@Parameter`, `@Tag`)
- **Native SQL** for complex aggregation queries (in `FlakyTestQueryRepository`), not JPQL
- **Modern Java features** encouraged (pattern matching, records, sealed types)

## Code Conventions — TypeScript

- **Functional components** only, with React hooks (`useState`, `useEffect`, `useCallback`, `useMemo`)
- **Strict mode** — all strict checks enabled in `tsconfig.json`
- **Single quotes** for imports, ESM modules (`"type": "module"`)
- **Named exports** for components (except `App` which is default)
- **MUI 7** with custom dark theme (teal/navy palette)
- **Interface-based props** for component contracts

## Package Structure

```
mura-dashboard/src/main/java/com/github/muradashboard/app/
  config/                              # Spring config (SecurityConfig, SPA forwarding)
  testreport/                          # Domain: controllers, services, repositories, entities, DTOs
    entity/                            # JPA entities (TestReportEntity, TestSuiteEntity, TestCaseEntity)
    dto/                               # Request/response records
  presentation/rest/                   # Frontend API controllers (/rapi/**)
    testreport/                        # Read API layer (FlakyTestController + DTOs)
      dto/

mura-dashboard/frontend/src/           # React SPA source
mura-dashboard/src/main/resources/
  db/migration/                        # Flyway SQL migrations (V1__, V2__, ...)
  static/                              # Frontend build output (generated)
```

## Database

- **PostgreSQL 18** — local via `docker-compose.yaml`
- **Flyway** migrations in `src/main/resources/db/migration/`
- **Snake_case** column/table names, mapped by JPA default naming strategy
- Tables: `test_report`, `test_suite`, `test_case`

## Helm Chart

Located in `mura-dashboard-helm-chart/`. Uses Bitnami PostgreSQL ~18 as a subchart.

```bash
# Validate chart (lint + template + kubeconform against K8s 1.32.0)
cd mura-dashboard-helm-chart
./ci-test.sh
```

## CI/CD

Three GitHub Actions workflows:

| Workflow | Trigger | Purpose |
|---|---|---|
| `release.yml` | `v*` tag push | Build + push Docker image to Docker Hub |
| `helm-chart.yml` | PR/push (chart changes) | Helm lint + template + kubeconform |
| `helm-chart-publish.yml` | `chart-v*` tag push | Package + push Helm chart to GHCR OCI |

Dependabot monitors `github-actions`, `gradle`, and `npm` dependencies daily.

## Security

- `/api/**` endpoints require HTTP Basic authentication (configured in `SecurityConfig`)
- `/rapi/**` endpoints are public, for the frontend
- Never commit secrets — credentials are injected via environment variables (Docker Compose) or Kubernetes Secrets (Helm chart)
- SQL injection protection: sort columns are allowlisted in `FlakyTestQueryRepository`

## Tooling Notes

No linters or formatters are configured (no ESLint, Prettier, Checkstyle, or Spotless). Code quality relies on the TypeScript compiler (strict mode) and the Java compiler. Follow existing code style when making changes.