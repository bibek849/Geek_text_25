# Geek Text API - Team 25

Spring Boot backend for CEN 4010 Geek Text project. This repository now includes the full Sprint 2 minimum backend deliverables.

## Sprint 2 Status

Implemented:
- Book entity and table mapping (`books`)
- PostgreSQL datasource configuration
- Dummy data seeding at startup (10 books)
- Example REST endpoint: `GET /api/books`

## Tech Stack

- Java 21
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven

## Team Ownership (from Sprint 1)

- Book Browsing and Sorting: Jairo Zapata
- Book Details: Dereck Zolotoff
- Profile Management: Bibek Yadav
- Shopping Cart: Anna Zaidze

## Code Structure

```text
src/main/java/com/bookstore/geek_text/
├── GeekTextApplication.java
└── book/
    ├── Book.java
    ├── BookRepository.java
    ├── BookController.java
    └── BookDataSeeder.java

src/main/resources/
└── application.properties
```

## Implemented API

### Get All Books

- Method: `GET`
- Path: `/api/books` (also available as `/books`)
- Response: `200 OK` + JSON array

Example:

```json
[
  {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "genre": "Software Engineering",
    "price": 39.99,
    "copiesSold": 8500
  }
]
```

## Detailed Setup Instructions

### 1. Prerequisites

Install:
- JDK 21
- PostgreSQL (14+ recommended)
- Maven 3.9+

Check versions:

```bash
java -version
mvn -version
```

### 2. Create Database and User

Open PostgreSQL and run:

```sql
CREATE DATABASE geek_text;
CREATE USER geek_text_user WITH ENCRYPTED PASSWORD 'change_me';
GRANT ALL PRIVILEGES ON DATABASE geek_text TO geek_text_user;
```

### 3. Configure Application

Default config in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/geek_text
spring.datasource.username=geek_text_user
spring.datasource.password=Geektext@master
spring.jpa.hibernate.ddl-auto=update
```

You can override values via environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/geek_text
export SPRING_DATASOURCE_USERNAME=geek_text_user
export SPRING_DATASOURCE_PASSWORD=Geektext@master
```

### 4. Run the Backend

Current repository issue:
- `./mvnw` cannot run because `.mvn/wrapper/*` is missing.

So use Maven directly:

```bash
mvn spring-boot:run
```

If you want wrapper support restored:

```bash
mvn -N wrapper:wrapper
```

Then you can run:

```bash
./mvnw spring-boot:run
```

### 5. Verify Sprint 2 Acceptance Criteria

#### A. App startup

Confirm app starts cleanly and listens on port `8080`.

#### B. Table creation

Run this SQL:

```sql
SELECT id, title, author, genre, price, copies_sold FROM books;
```

Expected:
- `books` table exists
- 10 seeded rows exist after first startup

#### C. Endpoint test (curl)

```bash
curl -s http://localhost:8080/api/books
curl -s http://localhost:8080/books
```

