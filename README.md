# TeamSync – Daily Standup Automation Backend

A backend system for IT teams to automate daily standups, track progress, and manage team reporting.

## Tech Stack
- Java 17, Spring Boot
- Spring Security, JWT Authentication
- Spring Data JPA, Hibernate
- PostgreSQL
- Maven

## Features
- JWT-based authentication with role-based access control (TEAM_MEMBER, TEAM_LEAD, MANAGER)
- Standup submission and team management APIs
- Input validation and global exception handling
- Clean layered architecture (controller → service → repository)

## How to Run Locally
1. Clone the repo
2. Configure PostgreSQL and update `application.properties`
3. Run `mvn spring-boot:run`

## Status
🚧 In Progress – Authentication and role-based access control complete.
Remaining: Search API, reporting endpoints, Swagger documentation.