# TeamSync - Daily Standup Automation Backend

A production-ready REST API backend for IT teams to automate daily standups, manage team structures, and generate progress reports, built with Java 17 and Spring Boot.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow?style=flat-square)
![Tests](https://img.shields.io/badge/Tests-44%20passing-success?style=flat-square)
![Java CI](https://github.com/NMB99/TeamSync/actions/workflows/ci.yml/badge.svg)

[//]: # (![License]&#40;https://img.shields.io/badge/License-MIT-lightgrey?style=flat-square&#41;)

---

## What It Does

TeamSync eliminates manual standup coordination in IT teams. Team members submit their daily standups via API, team leads and managers get filtered views of their team's progress, and reports are aggregated automatically, all secured with JWT authentication and role-based access control.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security, JWT (JJWT) |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Validation |
| Testing | JUnit 5, Mockito |
| Build Tool | Maven |

---

## Architecture

```
┌──────────────────────────────────────────┐
│              REST Controllers            │
│   (AuthController, UserController,       │
│    TeamController, StandupController,    │
│    ReportController)                     │
└────────────────┬─────────────────────────┘
                 │
┌────────────────▼─────────────────────────┐
│             Service Layer                │
│   (Business logic, role-based access,    │
│    ownership validation)                 │
└────────────────┬─────────────────────────┘
                 │
┌────────────────▼─────────────────────────┐
│           Repository Layer               │
│       (Spring Data JPA interfaces)       │
└────────────────┬─────────────────────────┘
                 │
┌────────────────▼─────────────────────────┐
│            PostgreSQL Database           │
└──────────────────────────────────────────┘

Security: JWT Auth Filter intercepts every request
          before reaching controllers
```

---

## Features

- **JWT Authentication** - stateless token-based auth with HS512 signing
- **Role-Based Access Control** - four roles with fine-grained permissions
- **Standup Management** - submit, update, delete daily standups with ownership enforcement
- **Team Management** - create and manage teams with member assignments
- **Report Generation** - team standup reports with optional date range filtering
- **Global Exception Handling** - consistent error responses across all endpoints
- **Input Validation** - request-level validation via Jakarta Validation annotations
- **Unit Tests** - 44 tests covering service layer with JUnit 5 and Mockito

---

## Role Permissions

| Action              | TEAM_MEMBER | TEAM_LEAD   | MANAGER | ADMIN |
|---------------------|--|-------------|---|---|
| Submit standup      | ✅ own only | ✅ own only  | ✅ own only | ❌ |
| View standups       | ✅ own only | ✅ team only | ✅ team only | ❌ |
| View teams          | ✅ own team | ✅ own team  | ✅ all teams | ❌ |
| View all users      | ❌ | ✅ own team  | ✅ all users | ✅ all |
| View user by ID     | ✅ | ✅           | ✅ | ✅ |
| Create/manage users | ❌ | ❌           | ❌ | ✅ |
| Create/manage teams | ❌ | ❌           | ✅ | ❌ |
| View reports        | ❌ | ✅ own team  | ✅ own team | ❌ |

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/login` | Login and receive JWT token | Public |

### Users
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/users` | Create user | ADMIN |
| GET | `/api/users` | Get all users | ADMIN, MANAGER, TEAM_LEAD |
| GET | `/api/users/{id}` | Get user by ID | Authenticated |
| PATCH | `/api/users/{id}` | Update user | ADMIN |
| DELETE | `/api/users/{id}` | Delete user | ADMIN |

### Teams
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/teams` | Create team | MANAGER |
| GET | `/api/teams` | Get teams | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| GET | `/api/teams/{id}` | Get team by ID | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| PATCH | `/api/teams/{id}` | Update team | MANAGER |
| DELETE | `/api/teams/{id}` | Delete team | MANAGER |

### Standups
| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/standups` | Submit standup | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| GET | `/api/standups` | Get standups | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| GET | `/api/standups/{id}` | Get standup by ID | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| PATCH | `/api/standups/{id}` | Update standup | MANAGER, TEAM_LEAD, TEAM_MEMBER |
| DELETE | `/api/standups/{id}` | Delete standup | MANAGER, TEAM_LEAD, TEAM_MEMBER |

### Reports
| Method | Endpoint                 | Description             | Access |
|---|--------------------------|-------------------------|---|
| GET | `/api/reports`           | Get team standup report | MANAGER, TEAM_LEAD |

> Query params: `teamId` (required), `startDate` (optional), `endDate` (optional)  
> Example: `/api/reports?teamId=1&startDate=2026-04-01&endDate=2026-04-30`
---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### Setup

**1. Clone the repository**
```bash
git clone https://github.com/NMB99/TeamSync.git
cd TeamSync
```

**2. Create PostgreSQL database**
```sql
CREATE DATABASE teamsync;
```

**3. Configure application.properties**

Copy the template and fill in your values:
```properties
spring.application.name=TeamSync

spring.datasource.url=jdbc:postgresql://localhost:5432/teamsync
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=YOUR_JWT_SECRET_MIN_32_CHARS
jwt.expiration=86400000
```

**4. Run the application**
```bash
mvn spring-boot:run
```

API will be available at `http://localhost:8080`

---

## Running Tests

```bash 
mvn test
```

44 unit tests covering UserService, TeamService and StandupService — including happy path, exception handling, and role-based access scenarios.

---

## Project Structure

```
src/main/java/com/teamsync/teamsync/
├── config/          # Security configuration
├── controller/      # REST controllers
├── dto/             # Request and response DTOs
├── entity/          # JPA entities (User, Team, Standup)
├── enums/           # Role, TeamCategory enums
├── exception/       # Custom exceptions and global handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, util, custom user details
├── service/         # Business logic layer
└── util/            # Utility classes (PasswordUtil)
```

---

## Upcoming Features

- [ ] Scheduled daily email reports (7pm) to TEAM_LEAD and MANAGER
- [ ] PDF report generation
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Swagger/OpenAPI documentation

---

## Author

**Nilay Bhaisare**  

MSc Computer Science Graduate - University of Bristol

[LinkedIn](https://linkedin.com/in/nilay-bhaisare) · [GitHub](https://github.com/NMB99)
