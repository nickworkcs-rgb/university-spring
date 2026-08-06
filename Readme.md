# University Management API

A Spring Boot REST API for managing university faculties, students, and books, built as a portfolio project.

## Tech Stack

- **Java 21**, **Spring Boot 4.1.0**
- **Spring MVC** — REST layer
- **Spring Data JPA** / Hibernate
- **PostgreSQL** — database
- **Liquibase** — database migrations
- **MapStruct** — entity/DTO mapping
- **Lombok**
- **springdoc-openapi** — Swagger UI / OpenAPI docs
- **JUnit 5 / Mockito** — unit tests
- **Docker / Docker Compose** — containerized local setup

## Features

- CRUD operations for **Faculties**, **Students**, and **Books**
- Assigning and unassigning books to/from students
- Bean validation on all incoming DTOs (Java records + Jakarta Validation)
- Centralized error handling via `@RestControllerAdvice` with consistent error responses
- Interactive API documentation via Swagger UI

## Architecture

The project follows a standard layered architecture:

```
Liquibase changelogs → JPA entities → Repositories → DTOs → MapStruct mappers → Controllers
```

- Entities use surrogate primary keys.
- Deleting a faculty sets the related students' faculty reference to `NULL` (`ON DELETE SET NULL`).
- Foreign key lookups (e.g. resolving a faculty by id when creating a student) are handled in the service layer, not in the mappers.

## Running with Docker

The application can be run fully containerized, with PostgreSQL included — no local Java or Postgres installation required.

### Prerequisites

- Docker and Docker Compose installed and running

### Setup

1. Create a `.env` file in the project root with the following variables:

   ```
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   ```

2. Build and start the application and database:

   ```bash
   docker compose up --build
   ```

3. The API will be available at `http://localhost:8080`, with Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

Liquibase migrations run automatically on startup, so the database schema is created and ready without any manual steps.

### Stopping

```bash
docker compose down
```

Add `-v` to also remove the database volume (this deletes all data):

```bash
docker compose down -v
```

## Running Locally (without Docker)

1. Have a local PostgreSQL instance running, with a database named `university`.
2. Set the `DB_USERNAME` and `DB_PASSWORD` environment variables — a `.env` file in the project root is picked up automatically (via `spring-dotenv`).
3. Run:

   ```bash
   ./mvnw spring-boot:run
   ```

## Running Tests

```bash
./mvnw test
```

Includes unit tests for the service layer (Mockito) and the controller layer (`@WebMvcTest`).

## API Overview

| Resource   | Endpoints                                                                                                    |
|------------|---------------------------------------------------------------------------------------------------------------|
| Faculties  | `GET /api/faculties`, `GET /api/faculties/{id}`, `POST /api/faculties`, `PUT /api/faculties/{id}`, `DELETE /api/faculties/{id}` |
| Students   | `GET /api/students`, `GET /api/students/{id}`, `POST /api/students`, `PUT /api/students/{id}`, `DELETE /api/students/{id}` |
| Books      | `GET /api/books`, `GET /api/books/{id}`, `POST /api/books`, `PUT /api/books/{id}`, `DELETE /api/books/{id}`, `PATCH /api/books/{id}/assign/{studentId}`, `PATCH /api/books/{id}/unassign` |

Full interactive documentation is available via Swagger UI once the app is running.