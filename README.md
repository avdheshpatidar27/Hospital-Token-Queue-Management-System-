# Hospital-Token-Queue-Management-System-
Built a Spring Boot REST API for hospital token/queue management with MySQL persistence, implementing a priority-based patient-calling system using Java's PriorityQueue and custom Comparator logic Applied Spring Data JPA, constructor-based Dependency Injection &amp; a soft-delete data pattern across a layered Controller-Service-Repository architecture


# Hospital Token Queue Management System

A RESTful backend that manages patient tokens at a hospital reception —
issue a token, queue patients by priority, call the next patient, and mark
consultations complete. Built as a hands-on project to practice Java OOP,
the Collections Framework, basic DSA, Spring Core (Dependency Injection),
Spring Data JPA, and MySQL.

## What it does

Think of the token board at a hospital or bank: a patient checks in, gets a
token number, and waits until they're called — with emergency/priority
patients getting seen first regardless of when they arrived. This project
implements exactly that logic as a REST API, backed by a MySQL database.

## Tech Stack

- **Java 17**
- **Spring Boot 3** (Spring Web, Spring Data JPA)
- **MySQL**
- **Maven**
- Core Java: Collections Framework (`List`, `Map`, `HashMap`),
  `PriorityQueue` + `Comparator` for the queue-ordering logic

## Features

- Issue a new token for a patient, with automatic token numbering
- List all currently active tokens
- Look up, update, or soft-delete a specific token
- Restore a soft-deleted token
- Call the next patient — priority patients are served first; among
  patients of equal priority, whoever arrived earlier goes next
- Mark a patient's consultation as completed
- View the current waiting queue grouped by department

## Project Structure

```
src/main/java/com/hospitalqueue/
├── HospitalQueueApplication.java   # Entry point
├── model/
│   ├── Token.java                  # @Entity — the core data object
│   └── TokenStatus.java            # Enum: WAITING, IN_CONSULTATION, COMPLETED
├── repository/
│   └── TokenRepository.java        # Spring Data JPA interface
├── service/
│   └── TokenService.java           # Business logic + queue ordering
└── controller/
    └── TokenController.java        # REST endpoints
```

**Layered architecture:** Controller → Service → Repository, wired together
using Spring's constructor-based Dependency Injection.

## Getting Started

### Prerequisites
- Java 17+
- Maven (or use IntelliJ's bundled Maven)
- MySQL running locally

### Setup

1. Clone or extract this project, and open it in IntelliJ (or your IDE of
   choice) as a Maven project.
2. Open `src/main/resources/application.properties` and set your own MySQL
   credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hospital_queue_db?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   The database itself will be created automatically on first run
   (`createDatabaseIfNotExist=true`) — you don't need to create it by hand.
3. Run `HospitalQueueApplication.java`. Hibernate will auto-create the
   `tokens` table on startup.
4. The API is now live at `http://localhost:8080`.

## API Reference

Base URL: `http://localhost:8080/api/tokens`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/tokens` | Create a new token |
| GET | `/api/tokens` | List all active tokens |
| GET | `/api/tokens/{id}` | Get a specific token |
| PUT | `/api/tokens/{id}` | Update a token's details |
| DELETE | `/api/tokens/{id}` | Soft-delete a token |
| PATCH | `/api/tokens/{id}/restore` | Restore a soft-deleted token |
| PATCH | `/api/tokens/call-next` | Call the next patient in priority order |
| PATCH | `/api/tokens/{id}/complete` | Mark a consultation as completed |
| GET | `/api/tokens/grouped-by-department` | View the waiting queue grouped by department |

### Example: Create a token

`POST /api/tokens`
```json
{
  "patientName": "Sunita Devi",
  "age": 70,
  "gender": "Female",
  "department": "Cardiology",
  "priority": true
}
```

**Response**
```json
{
  "id": 1,
  "patientName": "Sunita Devi",
  "age": 70,
  "gender": "Female",
  "department": "Cardiology",
  "tokenNumber": 1,
  "priority": true,
  "status": "WAITING",
  "deleted": false,
  "createdAt": "2026-07-18T10:15:30"
}
```

## The Queue Logic

The core of this project is `callNextPatient()` in `TokenService.java`. It
pulls all `WAITING` tokens into a Java `PriorityQueue`, ordered by a custom
`Comparator`:

1. Priority patients (`priority: true`) are ranked ahead of regular
   patients.
2. Among patients with equal priority, the one with the lower
   `tokenNumber` (i.e. checked in earlier) goes first.

This means calling `/api/tokens/call-next` always correctly returns the
patient who should be seen next — even if a priority patient checked in
after several regular patients.

## What's Deliberately Not Included (Yet)

This project was scoped to match a specific set of concepts — Java OOP,
Collections, basic DSA, MySQL, Spring Core, and Spring Data JPA — so the
following are intentionally left out:

- Authentication / authorization (Spring Security, JWT)
- Entity relationships (e.g. a separate `Department` or `Doctor` entity)
- Request validation (Bean Validation)
- Automated tests

### Possible next steps

- Extract `Department` into its own entity with a `@OneToMany` relationship
  to `Token`
- Add Spring Security so only staff can call `/call-next` or `/complete`
- Add `@Valid` request validation (e.g. reject a blank `patientName`)
- Add JUnit tests for the queue-ordering logic
- Containerize with Docker

## Skills Demonstrated

| Area | Where |
|---|---|
| OOP (encapsulation, enums) | `Token.java`, `TokenStatus.java` |
| Collections Framework | `List`, `HashMap`, `PriorityQueue` in `TokenService.java` |
| Basic DSA | Heap-based priority queue with a custom multi-level `Comparator` |
| Spring Core (Dependency Injection) | Constructor injection across all layers |
| Spring Data JPA | `@Entity`, `@Enumerated`, `JpaRepository`, derived query methods |
| MySQL | Persistent storage, auto-created schema |
| REST API design | Correct use of POST/GET/PUT/PATCH/DELETE |
| Soft delete pattern | `softDeleteToken()` / `restoreToken()` |
