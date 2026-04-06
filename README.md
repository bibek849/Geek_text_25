# Geek Text API - Team 25

Spring Boot backend for the CEN 4010 Geek Text project.

Code coverage for Sprint 5:
- Book browsing and sorting
- Book details and author management
- Profile management and credit cards
- Shopping cart
- Book ratings and comments
- Wishlist management

## Sprint Status

### Sprint 4 code coverage
- `Book Browsing and Sorting`: genre filter, top sellers, minimum rating filter, publisher discount update
- `Book Details`: create book, retrieve by ID/ISBN, create author, list books by author
- `Profile Management`: create user, retrieve by username, update user fields except email, add credit card
- `Shopping Cart`: add item, list cart items, compute subtotal, delete item
- `Book Rating and Commenting`: create rating, create comment, list comments, get average rating
- `Wish List Management`: create wishlist, add book, list wishlist books, move wishlist item to cart

## Tech Stack

- Java 21+ (`22.0.2` verified locally)
- Spring Boot 4.0.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL for local runtime
- H2 for test runtime
- Maven / Maven Wrapper

## Team Ownership

- Book Browsing and Sorting: Jairo Zapata
- Book Details: Dereck Zolotoff
- Profile Management: Bibek Yadav
- Shopping Cart: Anna Zaidze

## Code Structure

```text
src/main/java/com/bookstore/geek_text/
├── author/
├── book/
├── cart/
├── profile/
├── review/
└── wishlist/
```

## Setup

### 1. Prerequisites

Install:
- JDK 21 or newer
- PostgreSQL 14+

Verify Java:

```bash
java -version
```

If your shell is using an older JDK, switch to a newer one first:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 22)
export PATH="$JAVA_HOME/bin:$PATH"
```

### 2. Create Database and User

Run in PostgreSQL:

```sql
CREATE DATABASE geek_text;
CREATE USER geek_text_user WITH ENCRYPTED PASSWORD 'Geektext@master';
GRANT ALL PRIVILEGES ON DATABASE geek_text TO geek_text_user;
```

### 3. Runtime Configuration

Defaults in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/geek_text
spring.datasource.username=geek_text_user
spring.datasource.password=Geektext@master
spring.jpa.hibernate.ddl-auto=update
```

Optional overrides:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/geek_text
export SPRING_DATASOURCE_USERNAME=geek_text_user
export SPRING_DATASOURCE_PASSWORD=Geektext@master
```

### 4. Run the Backend

```bash
./mvnw spring-boot:run
```

App starts at `http://localhost:8080`.

## API Overview

### Books

- `GET /api/books`
  - Optional query params: `genre`, `minRating`
- `GET /api/books/top-sellers`
- `GET /api/books/{id}`
- `GET /api/books/isbn/{isbn}`
- `POST /api/books`
- `PATCH /api/books/discount?publisher=...&percent=...`

### Authors

- `POST /api/authors`
- `GET /api/authors/{authorId}/books`

### Users / Profile

- `POST /api/users`
- `GET /api/users/{username}`
- `PATCH /api/users/{username}`
- `POST /api/users/{username}/credit-cards`

### Cart

- `POST /api/cart/add`
- `GET /api/cart/{userId}`
- `GET /api/cart/{userId}/subtotal`
- `DELETE /api/cart/{userId}/books/{bookId}`

### Ratings / Comments

- `POST /api/books/{bookId}/ratings`
- `GET /api/books/{bookId}/ratings/average`
- `POST /api/books/{bookId}/comments`
- `GET /api/books/{bookId}/comments`

### Wishlists

- `POST /api/users/{userId}/wishlists`
- `POST /api/wishlists/{wishlistId}/books/{bookId}`
- `GET /api/wishlists/{wishlistId}`
- `GET /api/wishlists/{wishlistId}/books`
- `DELETE /api/wishlists/{wishlistId}/books/{bookId}/move-to-cart`

## Sample Requests

```bash
BASE=http://localhost:8080

curl -i "$BASE/api/books?genre=Java"
curl -i "$BASE/api/books/top-sellers"
curl -i "$BASE/api/books/1"
curl -i "$BASE/api/books/isbn/9780132350884"
curl -i -X PATCH "$BASE/api/books/discount?publisher=Addison-Wesley&percent=10"

curl -i -X POST "$BASE/api/authors" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Robert","lastName":"Martin","biography":"Software craftsman.","publisher":"Prentice Hall"}'

curl -i -X POST "$BASE/api/users" \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"Password123!","name":"Alice Carter","email":"alice@example.com","homeAddress":"123 Main St"}'

curl -i -X POST "$BASE/api/cart/add" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"bookId":1,"quantity":2}'
curl -i "$BASE/api/cart/1/subtotal"

curl -i -X POST "$BASE/api/books/1/ratings" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"rating":5}'
curl -i "$BASE/api/books/1/ratings/average"

curl -i -X POST "$BASE/api/users/1/wishlists" \
  -H "Content-Type: application/json" \
  -d '{"name":"Favorites"}'
```

## Test Commands

Run the full test suite:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 22)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw test
```

