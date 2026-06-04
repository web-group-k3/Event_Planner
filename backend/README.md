# EventSync — Backend

> A Spring Boot 4.0.4 / Java 21 REST API for the EventSync event management platform.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Backend](#running-the-backend)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [API Endpoints Overview](#api-endpoints-overview)
- [Project Structure](#project-structure)

---

## Prerequisites

Make sure you have the following installed on your machine:

| Tool          | Version / Notes            |
|---------------|----------------------------|
| **Java**      | 21 (JDK 21+)               |
| **Maven**     | 3.9+ (comes with the wrapper) |
| **PostgreSQL**| 14+                        |

Verify your installation:

```bash
java -version
mvn -version
psql --version
```

---

## Database Setup

### 1. Create the database

```bash
createdb eventplanner
```

Or via the `psql` shell:

```sql
CREATE DATABASE eventplanner;
```

### 2. Run the seed script

The file [`seed.sql`](../seed.sql) at the project root contains the full schema definition and sample data. Execute it against your database:

```bash
psql -d eventplanner -f /path/to/Event_Planner/seed.sql
```

If the database is on a remote host or requires authentication:

```bash
psql -h localhost -U postgres -d eventplanner -f seed.sql
```

### Schema Overview

The database consists of 7 tables, all using auto-increment integer primary keys (`SERIAL`):

| Table              | Description                                     | Key Columns                                      |
|--------------------|-------------------------------------------------|--------------------------------------------------|
| `admin`            | Admin users (authentication)                    | `id`, `username`, `password_hash`                |
| `event`            | Events / conferences                            | `id`, `title`, `description`, `start_date`, `end_date`, `location` |
| `room`             | Rooms / venues within an event                  | `id`, `name`, `event_id` (FK → event)            |
| `speaker`          | Speakers presenting at an event                 | `id`, `full_name`, `photo_url`, `bio`, `links` (JSONB), `event_id` (FK → event) |
| `session`          | Individual sessions within an event             | `id`, `title`, `description`, `start_time`, `end_time`, `capacity`, `event_id` (FK → event), `room_id` (FK → room) |
| `session_speaker`  | Many-to-many join between sessions and speakers | `session_id`, `speaker_id` (composite PK)        |
| `question`         | Audience questions for a session                | `id`, `content`, `author_name`, `upvotes`, `created_at`, `session_id` (FK → session) |

The seed script also inserts sample data: 3 events, 6 rooms, 8 speakers, 10 sessions, and several questions. An admin user is created with username `admin` and password `admin123`.

---

## Configuration

The backend uses [`spring-dotenv`](https://github.com/paulschwarz/spring-dotenv) to load environment variables from a `.env` file.

### Environment Variables

Create a `.env` file in `backend/examen/` (a template already exists there). The following variables are required:

| Variable        | Default Value                     | Description                          |
|-----------------|-----------------------------------|--------------------------------------|
| `DB_URL`        | `jdbc:postgresql://localhost:5432/eventplanner` | JDBC connection URL  |
| `DB_USERNAME`   | `postgres`                        | PostgreSQL user                      |
| `DB_PASSWORD`   | *(your password)*                 | PostgreSQL password                  |
| `JWT_SECRET`    | *(your secret)*                   | Secret key used to sign JWT tokens   |

Example `.env` file:

```env
DB_URL=jdbc:postgresql://localhost:5432/eventplanner
DB_USERNAME=postgres
DB_PASSWORD=your_password_here
JWT_SECRET=a-very-long-secret-key-at-least-256-bits-long
```

### Alternative: Direct `application.properties`

If you prefer not to use `.env`, you can edit the values directly in [`application.properties`](examen/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/eventplanner
spring.datasource.username=postgres
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

> **Note:** The `ddl-auto=update` setting is present but the authoritative schema comes from `seed.sql`. Hibernate will only apply minor differences — the recommended approach is to manage the schema through the SQL script.

### CORS Configuration

By default, CORS is configured to allow requests from `http://localhost:3000`. You can adjust this in [`CorsConfig.java`](examen/src/main/java/com/k3/examen/config/CorsConfig.java) if your frontend runs on a different origin.

---

## Running the Backend

### Option 1: Using Maven (recommended)

Navigate to the `backend/examen/` directory and run:

```bash
cd backend/examen
mvn clean install
mvn spring-boot:run
```

The API will start at **`http://localhost:8080/api`**.

### Option 2: Using an IDE

Open the `backend/examen/` folder in your IDE (IntelliJ, Eclipse, VS Code) and run the main class:

```
src/main/java/com/k3/examen/ExamenApplication.java
```

Make sure the `backend/examen/` directory is set as the working directory so the `.env` file is picked up.

---

## API Documentation

Interactive API documentation is available via **Swagger UI** (powered by SpringDoc OpenAPI):

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

The Swagger UI includes a JWT bearer token authentication option so you can test protected endpoints directly from the browser.

---

## Authentication

### Public (unauthenticated) endpoints

All `GET` requests are publicly accessible:

- `GET /api/events/**`
- `GET /api/sessions/**`
- `GET /api/speakers/**`
- `GET /api/rooms/**`
- `GET /api/questions/**`

Additionally, creating a question and upvoting are also public:

- `POST /api/questions`
- `POST /api/questions/{id}/upvote`

### Admin-only (JWT required)

All `POST`, `PUT`, `DELETE`, and `PATCH` requests require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

### Login

Obtain a token by posting credentials to the login endpoint:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

A successful response returns the JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Use this token in subsequent requests:

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{"title": "New Event", "description": "...", ...}'
```

---

## API Endpoints Overview

### Auth

| Method | Endpoint             | Auth Required | Description               |
|--------|----------------------|---------------|---------------------------|
| POST   | `/api/auth/login`    | No            | Authenticate and get JWT  |
| GET    | `/api/auth/me`       | Yes           | Get current user info     |

### Events

| Method | Endpoint                     | Auth Required | Description                      |
|--------|------------------------------|---------------|----------------------------------|
| GET    | `/api/events`                | No            | List all events                  |
| GET    | `/api/events/{id}`           | No            | Get event with its sessions      |
| GET    | `/api/events/byRoom/{roomId}`| No            | Get events by room               |
| GET    | `/api/events/bySpeaker/{speakerId}` | No     | Get events by speaker            |
| POST   | `/api/events`                | Yes           | Create a new event               |
| PUT    | `/api/events/{id}`           | Yes           | Update an event                  |
| DELETE | `/api/events/{id}`           | Yes           | Delete an event                  |

### Sessions

| Method | Endpoint                                 | Auth Required | Description                        |
|--------|------------------------------------------|---------------|------------------------------------|
| GET    | `/api/sessions`                          | No            | List all sessions                  |
| GET    | `/api/sessions/{id}`                     | No            | Get session detail                 |
| GET    | `/api/sessions/byEvent/{eventId}`        | No            | Get sessions by event              |
| GET    | `/api/sessions/byRoom/{roomId}`          | No            | Get sessions by room               |
| GET    | `/api/sessions/{eventId}/{roomId}`       | No            | Get sessions by event and room     |
| POST   | `/api/sessions`                          | Yes           | Create a session                   |
| PUT    | `/api/sessions/{id}`                     | Yes           | Update a session                   |
| DELETE | `/api/sessions/{id}`                     | Yes           | Delete a session                   |
| POST   | `/api/sessions/{id}/speakers/{speakerId}`| Yes           | Add speaker to session             |
| DELETE | `/api/sessions/{id}/speakers/{speakerId}`| Yes           | Remove speaker from session        |

### Speakers

| Method | Endpoint                        | Auth Required | Description                        |
|--------|---------------------------------|---------------|------------------------------------|
| GET    | `/api/speakers`                 | No            | List all speakers                  |
| GET    | `/api/speakers/{id}`            | No            | Get speaker with their sessions    |
| GET    | `/api/speakers/byRoom/{roomId}` | No            | Get speakers by room               |
| GET    | `/api/speakers/byEvent/{eventId}`| No           | Get speakers by event              |
| POST   | `/api/speakers`                 | Yes           | Create a speaker                   |
| PUT    | `/api/speakers/{id}`            | Yes           | Update a speaker                   |
| DELETE | `/api/speakers/{id}`            | Yes           | Delete a speaker                   |

### Rooms

| Method | Endpoint                        | Auth Required | Description                        |
|--------|---------------------------------|---------------|------------------------------------|
| GET    | `/api/rooms`                    | No            | List all rooms                     |
| GET    | `/api/rooms/{id}`               | No            | Get room by ID                     |
| GET    | `/api/rooms/byEvent/{eventId}`  | No            | Get rooms by event                 |
| GET    | `/api/rooms/bySpeaker/{speakerId}`| No          | Get rooms by speaker               |
| GET    | `/api/rooms/byAdress`           | No            | Get rooms by address               |
| POST   | `/api/rooms`                    | Yes           | Create a room                      |
| PUT    | `/api/rooms/{id}`               | Yes           | Update a room                      |
| DELETE | `/api/rooms/{id}`               | Yes           | Delete a room                      |

### Questions

| Method | Endpoint                     | Auth Required | Description                        |
|--------|------------------------------|---------------|------------------------------------|
| GET    | `/api/questions?sessionId=`  | No            | Get questions for a session        |
| POST   | `/api/questions?sessionId=`  | No            | Create a question for a session    |
| PATCH  | `/api/questions/{id}/content`| Yes           | Update question content            |
| DELETE | `/api/questions/{id}`        | Yes           | Delete a question                  |
| POST   | `/api/questions/{id}/upvote` | No            | Upvote a question                  |

---

## Project Structure

```
backend/
└── examen/
    ├── pom.xml                          # Maven configuration
    ├── .env                             # Environment variables (DB, JWT)
    └── src/main/java/com/k3/examen/
        ├── ExamenApplication.java       # Spring Boot entry point
        ├── config/
        │   ├── CorsConfig.java          # CORS settings
        │   ├── DatabaseConnection.java  # Raw JDBC connection helper
        │   ├── JwtFilter.java           # JWT authentication filter
        │   ├── JwtUtil.java             # JWT token utilities
        │   ├── SecurityConfig.java      # Spring Security configuration
        │   └── SwaggerConfig.java       # OpenAPI / Swagger configuration
        ├── controller/                  # REST controllers
        │   ├── AuthController.java
        │   ├── EventController.java
        │   ├── QuestionController.java
        │   ├── RoomController.java
        │   ├── SessionController.java
        │   └── SpeakerController.java
        ├── dto/                         # Data transfer objects
        ├── exception/                   # Exception handling
        ├── model/                       # JPA entities / domain models
        ├── repository/                  # Data access layer
        ├── service/                     # Business logic
        └── validator/                   # Validation logic
    └── src/main/resources/
        └── application.properties       # Spring Boot configuration
```
