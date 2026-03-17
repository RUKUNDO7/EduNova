# EduNova (Spring Boot)

EduNova is a modular Learning Management System (LMS) with a Spring Boot REST API in erride defaults with environment variables:

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

## Testing

```
cd backend
mvn -q test
```

> Tests currently fail in this environment because Maven lacks permission to create `C:\Users\CodexSandboxOffline\.m2\repository`. Ensure a writable Maven local repository exists before running.
