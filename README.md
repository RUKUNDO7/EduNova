# EduNova (Spring Boot + React)

EduNova is a modular Learning Management System (LMS) with a Spring Boot REST API in `backend/` and a React SPA in `frontend/`.

## Highlights
- **Core LMS**: Courses, lessons, materials, enrollments, and progress tracking.
- **User roles**: Admins, instructors, and students with role-aware permissions.
- **Course taxonomy**: Category management and instructor ownership per course.
- **Assignments**: Instructor-created assignments with student submissions and grading.
- **Quizzes**: Quiz creation, question/answer banks, and student attempts with scoring.
- **Discussion forum**: Course-based discussion threads and messages.
- **Postgres persistence** with Spring Data/JPA plus sample data (courses, quizzes, assignments, progress, and messages).
- **React frontend** under `frontend/` showcasing course discovery, discussions, assessments, and progress.

## Backend runtime

### Prerequisites
- Java 21 SDK
- PostgreSQL 14+ running locally or remotely

### Configuration
Spring Boot relies on `backend/src/main/resources/application.properties`. Override defaults with environment variables:

| Env var | Purpose | Default |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/edunova` |
| `SPRING_DATASOURCE_USERNAME` | DB user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `Rukundo` |

Ensure the configured user has privileges to create tables, or pre-create the schema.

### Commands

```
cd backend
mvn -q clean package
mvn spring-boot:run
```

Key endpoints:
- `/api/auth/register` and `/api/auth/login` for user onboarding.
- `/api/courses`, `/api/categories`, and lesson/material routes for course management.
- `/api/assignments` for assignments, submissions, and grading.
- `/api/quizzes` for quizzes, questions, and attempts.
- `/api/enrollments`, `/api/progress`, and `/api/communications` for the learner journey.
- SpringDoc UI: `/swagger-ui/index.html`

## React frontend

The UI lives in the `frontend/` directory and consumes the backend APIs.

### Quick start

```
cd frontend
npm install
npm run dev
```

Adjust the API base URL via `frontend/.env` (see template) if the backend runs on a non-default port.

## Testing

```
cd backend
mvn -q test
```

> Tests currently fail in this environment because Maven lacks permission to create `C:\Users\CodexSandboxOffline\.m2\repository`. Ensure a writable Maven local repository exists before running.
