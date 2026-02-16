# Geek Text API (Team 25)

Backend API for the Geek Text online bookstore project (CEN 4010), built with Spring Boot and PostgreSQL.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Current Repository Status](#current-repository-status)
3. [Tech Stack](#tech-stack)
4. [Team and Feature Ownership](#team-and-feature-ownership)
5. [Planned Sprint 2 Deliverables](#planned-sprint-2-deliverables)
6. [Repository Structure](#repository-structure)
7. [Prerequisites](#prerequisites)
8. [Local Setup](#local-setup)
9. [Run and Test](#run-and-test)
10. [Configuration](#configuration)
11. [Branching and Workflow](#branching-and-workflow)
12. [Documentation](#documentation)
13. [Troubleshooting](#troubleshooting)

## Project Overview
Geek Text is a web application project for an online bookstore that targets technology-focused books.

Core feature areas from the course checklist:
- Book Browsing and Sorting
- Profile Management
- Shopping Cart
- Book Details
- Book Rating and Commenting
- Wish List Management

## Current Repository Status
This repository currently contains a Spring Boot project scaffold and sprint documentation.

Implemented in code right now:
- Spring Boot application bootstrap class
- Maven dependency setup
- Basic application configuration file

Not yet present in this repo (as code):
- Entities/models (for example `Book`)
- Repositories/services/controllers
- Public REST endpoints (for example `GET /api/books`)
- Database seed scripts

## Tech Stack
- Java 21
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- PostgreSQL (runtime dependency)
- Maven build system

## Team and Feature Ownership
Team: Group 25

Feature ownership from Sprint 1 architecture summary:
- Book Browsing and Sorting: Jairo Zapata
- Book Details: Dereck Zolotoff
- Profile Management: Bibek Yadav
- Shopping Cart: Anna Zaidze
- Book Rating and Commenting: Unassigned
- Wish List Management: Unassigned

## Planned Sprint 2 Deliverables
Based on sprint planning docs:
- Ensure Spring Boot backend runs locally for all members
- Configure and validate PostgreSQL connectivity
- Create `Book` entity/table (data model)
- Seed database with dummy book records
- Implement and test an example `GET /api/books` endpoint
- Validate through Postman and record demo video

## Repository Structure
```text
.
├── src/main/java/com/bookstore/geek_text/
│   └── GeekTextApplication.java
├── src/main/resources/
│   └── application.properties
├── src/test/java/com/bookstore/geek_text/
│   └── GeekTextApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── HELP.md
└── _sprint_docs/
```

## Prerequisites
Install these before running locally:
- JDK 21
- PostgreSQL 14+ (or compatible version)
- Maven 3.9+ (currently required; see wrapper note below)

## Local Setup
1. Clone the repository.
2. Create a PostgreSQL database for this project.
3. Configure datasource settings in `src/main/resources/application.properties`.
4. Run the app.

Example PostgreSQL setup:
```sql
CREATE DATABASE geek_text;
CREATE USER geek_text_user WITH ENCRYPTED PASSWORD 'change_me';
GRANT ALL PRIVILEGES ON DATABASE geek_text TO geek_text_user;
```

## Run and Test
Because wrapper config is currently missing, use Maven directly after installing it.

Run application:
```bash
mvn spring-boot:run
```

Run tests:
```bash
mvn test
```

## Configuration
Current `application.properties` only contains:
```properties
spring.application.name=geek-text
```

Recommended local development config (adjust credentials):
```properties
spring.application.name=geek-text

spring.datasource.url=jdbc:postgresql://localhost:5432/geek_text
spring.datasource.username=geek_text_user
spring.datasource.password=change_me

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

## Branching and Workflow
Suggested workflow for this repo:
1. Create a feature branch from main.
2. Implement one scoped story per branch.
3. Open a pull request with:
   - Problem statement
   - Scope of changes
   - Test evidence (logs, screenshots, Postman output)
4. Merge after review.

Naming examples:
- `feature/book-entity`
- `feature/books-get-endpoint`
- `chore/db-seed-data`

## Documentation
Project documentation in this workspace:
- Sprint docs: `_sprint_docs/`
- Spring starter notes: `HELP.md`

## Troubleshooting
### `./mvnw` fails with missing wrapper properties
Current issue in this repo:
- `.mvn/wrapper/maven-wrapper.properties` is missing
- `.mvn/wrapper/maven-wrapper.jar` is missing

Fix options:
1. Install Maven and run:
```bash
mvn -N wrapper:wrapper
```
2. Or restore `.mvn/wrapper/` from a known good Spring Boot project.

After wrapper files are restored, these commands should work:
```bash
./mvnw spring-boot:run
./mvnw test
```

### PostgreSQL connection errors
- Verify PostgreSQL service is running.
- Confirm database/user/password values in `application.properties`.
- Confirm the database exists and user has privileges.

