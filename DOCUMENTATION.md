# EduNova Project Documentation

## Overview
EduNova is a modular Learning Management System (LMS) backend built with Spring Boot. It provides REST APIs for course management, assignments, quizzes, progress tracking, enrollments, and course discussions. The current repository contains the backend service only.

## Repository Layout
- `backend/` Spring Boot application.
- `backend/src/main/java/com/elearning/platform/` application code.
- `backend/src/main/resources/application.properties` runtime configuration.
- `backend/src/test/java/com/elearning/platform/` unit tests.

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Spring Validation
- PostgreSQL (runtime)
- Spring Security Crypto (BCrypt password hashing)
- SpringDoc OpenAPI UI

## Running Locally
Prerequisites:
- Java 17 SDK
- PostgreSQL 14+ (or any compatible PostgreSQL version)

Configuration in `backend/src/main/resources/application.properties`:
- `spring.datasource.url` defaults to `jdbc:postgresql://localhost:5432/edunova`
- `spring.datasource.username` defaults to `postgres`
- `spring.datasource.password` defaults to `Rukundo`
- `spring.jpa.hibernate.ddl-auto=update` to auto-create tables

Commands:
```bash
cd backend
mvn -q clean package
mvn spring-boot:run
```

OpenAPI UI:
- `http://localhost:8080/swagger-ui/index.html`

## Data Model Summary
Entities and their key relationships:
- `UserAccount` (role: `ADMIN`, `INSTRUCTOR`, `STUDENT`)
- `Category` has many `Course`
- `Course` belongs to `Category` and `UserAccount` (instructor), and has many `Lesson`, `Material`, `Assignment`, and `Quiz`
- `Lesson` belongs to `Course`
- `Material` belongs to `Course` and `UserAccount` (uploader); `MaterialType` is an enum
- `Assignment` belongs to `Course` and `UserAccount` (instructor)
- `Submission` belongs to `Assignment` and `UserAccount` (learner)
- `Quiz` belongs to `Course` and has many `Question`
- `Question` belongs to `Quiz` and has many `Answer`
- `QuizAttempt` belongs to `Quiz` and `UserAccount` (learner)
- `Enrollment` belongs to `Course` and `UserAccount` (learner)
- `ProgressRecord` belongs to `Lesson` and `UserAccount` (learner)
- `CommunicationThread` belongs to `Course` (optional) and `UserAccount` (creator)
- `Message` belongs to `CommunicationThread` and `UserAccount` (sender)

## Authentication And Authorization
The backend does not issue tokens. Instead, requests that require a role use a header:
- `X-Actor-Id`: the user ID to authorize the request

Role enforcement happens in the service layer. If the user is missing or their role is not permitted, the service throws `IllegalArgumentException`.

## API Reference
Base URL: `/api`

### Auth And Users
| Method | Path | Purpose | Request Body | Notes |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/register` | Register a user | `RegisterRequest` | Default role is `STUDENT` |
| POST | `/api/auth/login` | Authenticate | `AuthRequest` | Returns `UserDTO` |
| POST | `/api/auth/reset-password` | Reset password | `PasswordResetRequest` | |
| POST | `/api/auth/logout` | Logout | none | Returns `{ "status": "logged_out" }` |
| GET | `/api/auth/users` | List users | none | |

### Courses And Lessons
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| GET | `/api/courses` | List courses | none | Optional query: `level`, `includeDrafts` |
| GET | `/api/courses/{id}` | Course detail | none | |
| POST | `/api/courses` | Create course | `CourseCreateRequest` | `INSTRUCTOR` or `ADMIN` |
| PUT | `/api/courses/{id}` | Update course | `CourseUpdateRequest` | `INSTRUCTOR` or `ADMIN` |
| POST | `/api/courses/{id}/publish` | Publish/unpublish | `CoursePublishRequest` | `ADMIN` only |
| DELETE | `/api/courses/{id}` | Delete course | none | `ADMIN` only |
| POST | `/api/courses/{id}/lessons` | Add lesson | `LessonRequest` | No role check in code |

### Categories
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| GET | `/api/categories` | List categories | none | |
| POST | `/api/categories` | Create category | `CategoryRequest` | `ADMIN` |
| PUT | `/api/categories/{id}` | Update category | `CategoryRequest` | `ADMIN` |
| DELETE | `/api/categories/{id}` | Delete category | none | `ADMIN` |

### Materials
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| GET | `/api/materials/course/{id}` | List materials by course | none | |
| POST | `/api/materials/course/{id}` | Add material | `MaterialRequest` | `INSTRUCTOR` or `ADMIN` |
| GET | `/api/materials/types` | List material types | none | |

### Assignments And Submissions
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| GET | `/api/assignments/course/{courseId}` | List assignments | none | |
| POST | `/api/assignments/course/{courseId}` | Create assignment | `AssignmentRequest` | `INSTRUCTOR` or `ADMIN` |
| POST | `/api/assignments/{assignmentId}/submissions` | Submit assignment | `SubmissionRequest` | `STUDENT` |
| PATCH | `/api/assignments/submissions/{submissionId}` | Grade submission | `SubmissionGradeRequest` | `INSTRUCTOR` or `ADMIN` |
| GET | `/api/assignments/learner/{learnerId}` | List learner submissions | none | |

### Quizzes
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| GET | `/api/quizzes/course/{courseId}` | List quizzes | none | |
| GET | `/api/quizzes/{quizId}` | Quiz detail | none | |
| POST | `/api/quizzes/course/{courseId}` | Create quiz | `QuizRequest` | `INSTRUCTOR` or `ADMIN` |
| POST | `/api/quizzes/{quizId}/questions` | Add question | `QuestionRequest` | `INSTRUCTOR` or `ADMIN` |
| POST | `/api/quizzes/{quizId}/attempts` | Submit attempt | `QuizAttemptRequest` | `STUDENT` |
| GET | `/api/quizzes/learner/{learnerId}/attempts` | Learner attempts | none | |

### Enrollments And Progress
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| POST | `/api/enrollments` | Enroll learner | `EnrollmentRequest` | |
| GET | `/api/enrollments/learner/{learnerId}` | Learner enrollments | none | |
| POST | `/api/progress/lessons/{lessonId}/complete` | Record progress | `ProgressRequest` | `STUDENT` |
| GET | `/api/progress/learner/{learnerId}` | Progress report | none | |

### Communications
| Method | Path | Purpose | Request Body | Role |
| --- | --- | --- | --- | --- |
| POST | `/api/communications/threads` | Create thread | `ThreadRequest` | Any role |
| GET | `/api/communications/threads` | List threads | none | Optional query: `courseId` |
| POST | `/api/communications/threads/{threadId}/messages` | Post message | `MessageRequest` | Role not enforced |
| GET | `/api/communications/threads/{threadId}/messages` | List messages | none | |

## Request And Response DTOs
Key request payloads (fields are JSON properties):
- `RegisterRequest`: `name`, `email`, `password`, `role`
- `AuthRequest`: `email`, `password`
- `PasswordResetRequest`: `email`, `newPassword`
- `CourseCreateRequest`: `title`, `description`, `level`, `estimatedHours`, `instructorId`, `categoryId`
- `CourseUpdateRequest`: `title`, `description`, `level`, `estimatedHours`, `instructorId`, `categoryId`
- `CoursePublishRequest`: `published`
- `LessonRequest`: `title`, `summary`, `contentUrl`, `sequenceNumber`, `durationMinutes`
- `CategoryRequest`: `name`, `description`
- `MaterialRequest`: `title`, `description`, `resourceUrl`, `type`
- `AssignmentRequest`: `title`, `description`, `dueDate`, `maxScore`
- `SubmissionRequest`: `fileUrl`
- `SubmissionGradeRequest`: `grade`, `feedback`
- `QuizRequest`: `title`, `description`
- `QuestionRequest`: `questionText`, `position`, `answers` (list of `AnswerOptionRequest`)
- `AnswerOptionRequest`: `answerText`, `correct`
- `QuizAttemptRequest`: `answers` (list of `QuizAnswerRequest`)
- `QuizAnswerRequest`: `questionId`, `answerId`
- `EnrollmentRequest`: `learnerName`, `learnerEmail`, `courseId`
- `ProgressRequest`: `timeSpentMinutes`, `quizScore`, `completed`
- `ThreadRequest`: `topic`, `courseId`
- `MessageRequest`: `content`

Response DTOs include:
- `UserDTO`, `UserSummaryDTO`
- `CourseSummaryDTO`, `CourseDetailDTO`
- `LessonDTO`, `MaterialDTO`, `CategoryDTO`
- `AssignmentDTO`, `SubmissionDTO`
- `QuizDTO`, `QuizDetailDTO`, `QuestionDTO`, `AnswerDTO`, `QuizAttemptDTO`
- `ProgressReportDTO`

## Sample Data
On application startup, `SampleDataLoader` seeds data only if no courses exist. It creates:
- Categories: Programming, Mathematics, Design
- Users: admin, instructor, student (all with password `password`)
- Courses (published and draft), lessons, materials
- Assignment, submission, quiz, questions, answers, quiz attempt
- Progress record
- Discussion thread and messages

## Tests
There is a unit test for `CourseService` in `backend/src/test/java/com/elearning/platform/service/CourseServiceTest.java`.

## Known Notes And Limitations
- Authorization uses `X-Actor-Id` instead of auth tokens.
- Some endpoints return JPA entities directly (`Enrollment`, `CommunicationThread`, `Message`, `ProgressRecord`, `Material`) rather than DTOs.
- Lesson creation does not check roles in the service layer.
- Enrollment creates a student account with password `changeme` when the email is new.
- Exceptions are thrown as `IllegalArgumentException`; there is no custom error response envelope.
