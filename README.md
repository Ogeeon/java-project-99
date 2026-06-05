### Hexlet tests and linter status:
[![Actions Status](https://github.com/Ogeeon/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Ogeeon/java-project-99/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Ogeeon_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Ogeeon_java-project-99)

# Task Manager

An application for managing tasks with statuses, labels and assignees. Users authenticate
with JWT, and every task can be filtered by title contents, status, assignee or label.

## Live demo
Application is deployed on Render: https://java-project-99-y6e3.onrender.com

## Tech stack
- Java 21, Spring Boot 3
- Spring Web, Spring Data JPA
- Spring Security with JWT
- MapStruct (DTO mapping), springdoc OpenAPI (Swagger UI)
- Gradle (Kotlin DSL)
- H2 (development) / PostgreSQL (production)

## Run locally

Requirements: **JDK 21**.

```bash
./gradlew bootRun
```

The app starts with the `development` profile on a file-based H2 database and is
available at http://localhost:8080.

## Default credentials

On first start an admin user is created automatically:

| Email                 | Password |
|-----------------------|----------|
| `hexlet@example.com`  | `qwerty` |

You can override them with the `ADMIN_EMAIL` and `ADMIN_PASSWORD` environment variables.

## REST API

Authenticate to obtain a token:

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "hexlet@example.com", "password": "qwerty"}'
```

The endpoint returns a JWT. Pass it with every subsequent request:

```
Authorization: Bearer <token>
```

Main resources:

- `/api/users`
- `/api/task_statuses`
- `/api/labels`
- `/api/tasks`

Interactive documentation is available via Swagger UI at
[`/swagger-ui.html`](https://java-project-99-y6e3.onrender.com/swagger-ui.html)
(locally: http://localhost:8080/swagger-ui.html).
