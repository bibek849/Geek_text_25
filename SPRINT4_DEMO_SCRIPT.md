# Sprint 4 Demo Script (Group 25)

Use this as a read-aloud script during recording. Keep the video between 7 and 8 minutes.

## Demo Goal

Show Sprint 4 completion and integrated backend readiness:
- Backend starts successfully
- Book browsing and sorting endpoints work
- Book details endpoints work
- Shopping cart endpoints work
- Database-backed results can be shown from PostgreSQL
- Final smoke-test readiness is demonstrated

## Speaker Order

1. Jairo Zapata (Product Owner)
2. Dereck Zolotoff (Scrum Master)
3. Anna Zaidze (Developer 1 - Shopping Cart)
4. Bibek Yadav (Developer 2 - Validation / Integration)

## 0:00-0:45 Jairo (Intro + Sprint Scope)

### On screen
- Open project root in IDE
- Show Sprint 4 docs folder
- Show `README.md`

### Script
"Hello, we are Group 25 presenting Sprint 4 for Geek Text.  
For this sprint, our goal was to finish the remaining backend REST functionality, stabilize integration, validate the endpoints, and prepare for the demo.  
Today we will show backend startup, book browsing and sorting, book details, shopping cart flows, and final integration proof from the database and automated tests."

## 0:45-1:40 Dereck (Run App + Integration Stability)

### On screen
- Open terminal in project root
- Show Java version
- Start the app and wait for startup logs

### Commands
```bash
cd /Users/kraten/Geek_text_25
export JAVA_HOME=$(/usr/libexec/java_home -v 22)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
./mvnw spring-boot:run
```

### Script
"I am Dereck, Scrum Master for Sprint 4.  
I will start the application and show that the integrated backend runs successfully.  
The service starts on port 8080, connects to PostgreSQL, loads the seeded data, and is ready for endpoint validation.  
This satisfies our sprint requirement that the merged main branch runs without startup errors."

## 1:40-3:25 Jairo (Book Browsing and Sorting)

### On screen
- Show `src/main/java/com/bookstore/geek_text/book/BookController.java`
- Open Postman or a second terminal
- Call the browsing and sorting endpoints

### Commands
```bash
BASE=http://localhost:8080

curl -i "$BASE/api/books?genre=Java"
curl -i "$BASE/api/books/top-sellers"
curl -i "$BASE/api/books?minRating=4.5"
curl -i -X PATCH "$BASE/api/books/discount?publisher=Addison-Wesley&percent=10"
curl -i "$BASE/api/books?genre=Java"
```

### Script
"I am Jairo, Product Owner, and I handled Book Browsing and Sorting.  
First, we filter books by genre, which returns only matching books from the database.  
Next, we call the top-sellers endpoint, which returns the highest-selling books in descending order.  
Then we filter by minimum rating to show that only books meeting the threshold are returned.  
Finally, we apply a publisher discount and verify that the updated prices are reflected in the next response.  
This covers the main Sprint 4 browsing and sorting acceptance criteria."

## 3:25-4:35 Dereck (Book Details)

### On screen
- Keep terminal or Postman open
- Show book details requests by ID and ISBN
- Show one invalid request returning `404`

### Commands
```bash
BASE=http://localhost:8080

curl -i "$BASE/api/books/1"
curl -i "$BASE/api/books/isbn/9780132350884"
curl -i "$BASE/api/books/99999"
```

### Script
"Now I will show the Book Details endpoints.  
The first request retrieves a book by numeric ID, and the second retrieves a book by ISBN.  
Both responses return full book details from the database.  
We also run an invalid ID request, which correctly returns a 404 response.  
This demonstrates the expected book details behavior and basic input validation."

## 4:35-5:55 Anna (Shopping Cart Flow)

### On screen
- Show `src/main/java/com/bookstore/geek_text/cart/CartController.java`
- Add a book to cart
- Retrieve cart
- Retrieve subtotal
- Optionally remove item from cart

### Commands
```bash
BASE=http://localhost:8080

curl -i -X POST "$BASE/api/cart/add" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"bookId":1,"quantity":2}'

curl -i "$BASE/api/cart/1"
curl -i "$BASE/api/cart/1/subtotal"
curl -i -X DELETE "$BASE/api/cart/1/books/1"
curl -i "$BASE/api/cart/1"
```

### Script
"I am Anna, Developer 1, responsible for Shopping Cart work.  
Here we add a book to the shopping cart for a seeded user, and the response returns status 201 Created.  
Next, we retrieve the cart and confirm the item is stored and returned as JSON.  
We also retrieve the subtotal to show that the total price is calculated from the cart contents.  
Finally, we delete the item and confirm the updated cart response.  
This demonstrates the completed cart flow working against the database."

## 5:55-6:55 Bibek (Database Proof + Smoke Test Readiness)

### On screen
- Open `psql`, pgAdmin, or DBeaver
- Show query results for `books` and `cart_items`
- Run automated tests in a second terminal if time allows

### Commands
```bash
export PGPASSWORD=Geektext@master
psql -h localhost -U geek_text_user -d geek_text -c "SELECT id,title,publisher,price,average_rating FROM books ORDER BY id LIMIT 10;"
psql -h localhost -U geek_text_user -d geek_text -c "SELECT id,user_id,book_id,quantity FROM cart_items ORDER BY id;"

cd /Users/kraten/Geek_text_25
export JAVA_HOME=$(/usr/libexec/java_home -v 22)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw test
```

### Script
"I am Bibek, Developer 2, and I focused on validation and integration debugging.  
Here we are showing direct database proof that the API responses are backed by PostgreSQL data.  
The books table reflects the seeded and updated records, and the cart_items table reflects the cart operations we just performed.  


## 6:55-7:35 Jairo (Close + Sprint Outcome)

### On screen
- Keep Postman or terminal results visible
- Optionally show Sprint 4 review summary or README API section

### Script
"To summarize, Sprint 4 goals were achieved.  
We completed the remaining browsing, sorting, book details, and shopping cart work, validated the integrated backend, and prepared the project for the final sprint.  
The system is now stable, database-backed, and ready for final submission work in Sprint 5.  
Thank you."

## Backup Lines (If Something Fails Live)

- "If Postman is slow, we can use curl to show the same endpoint behavior immediately."
- "If the database UI takes too long to load, we can show SQL query output directly from terminal."
- "If a cart row already exists from a prior run, the API still proves persistence because the quantity updates in real time."
- "If prices were already discounted in an earlier run, we will still show that the PATCH endpoint updates database-backed values."

## Quick Recording Checklist

1. Start PostgreSQL before recording.
2. Confirm Java 22 or newer is active in the terminal.
3. Keep one terminal for the running app and another for curl or test commands.
4. Prepare Postman requests in the same order as the script.
5. Keep the database UI or `psql` already connected to avoid delays.
6. Do one dry run first so the cart and discount demo steps are predictable.
