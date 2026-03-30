package com.bookstore.geek_text.book;

import com.bookstore.geek_text.author.Author;
import com.bookstore.geek_text.author.AuthorRepository;
import com.bookstore.geek_text.profile.UserAccount;
import com.bookstore.geek_text.profile.UserAccountRepository;
import com.bookstore.geek_text.review.BookComment;
import com.bookstore.geek_text.review.BookCommentRepository;
import com.bookstore.geek_text.review.BookRating;
import com.bookstore.geek_text.review.BookRatingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Order(1)
public class BookDataSeeder implements CommandLineRunner {

    private static final BigDecimal ZERO_RATING = new BigDecimal("0.00");

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserAccountRepository userAccountRepository;
    private final BookRatingRepository bookRatingRepository;
    private final BookCommentRepository bookCommentRepository;

    public BookDataSeeder(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            UserAccountRepository userAccountRepository,
            BookRatingRepository bookRatingRepository,
            BookCommentRepository bookCommentRepository
    ) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.userAccountRepository = userAccountRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookCommentRepository = bookCommentRepository;
    }

    @Override
    public void run(String... args) {
        Map<String, Author> authorsByName = ensureAuthors();
        ensureBooks(authorsByName);
        List<UserAccount> users = ensureUsers();
        ensureRatings(users);
        ensureComments(users);
        refreshAverageRatings();
    }

    private Map<String, Author> ensureAuthors() {
        if (authorRepository.count() == 0) {
            authorRepository.saveAll(defaultAuthors());
        }

        return authorRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .collect(Collectors.toMap(Author::getFullName, Function.identity(), (left, right) -> left, LinkedHashMap::new));
    }

    private void ensureBooks(Map<String, Author> authorsByName) {
        if (bookRepository.count() == 0) {
            bookRepository.saveAll(defaultBooks(authorsByName));
            return;
        }

        backfillExistingBooks(authorsByName);
    }

    private List<UserAccount> ensureUsers() {
        if (userAccountRepository.count() == 0) {
            userAccountRepository.saveAll(List.of(
                    new UserAccount("reader1", "Password123!", "Alice Carter", "alice@example.com", "123 Main St"),
                    new UserAccount("reader2", "Password123!", "Brandon Lee", "brandon@example.com", "456 Oak Ave"),
                    new UserAccount("reader3", "Password123!", "Carmen Diaz", "carmen@example.com", "789 Pine Rd")
            ));
        }

        return userAccountRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    private void ensureRatings(List<UserAccount> users) {
        if (bookRatingRepository.count() > 0 || users.size() < 3 || bookRepository.count() == 0) {
            return;
        }

        List<Book> books = bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        int[][] ratings = {
                {5, 4, 5},
                {5, 5, 4},
                {4, 5, 4},
                {5, 4, 4},
                {4, 4, 5},
                {5, 5, 5},
                {4, 4, 4},
                {5, 4, 3},
                {4, 3, 4},
                {3, 4, 4}
        };

        for (int bookIndex = 0; bookIndex < books.size() && bookIndex < ratings.length; bookIndex++) {
            Book book = books.get(bookIndex);
            for (int userIndex = 0; userIndex < users.size() && userIndex < ratings[bookIndex].length; userIndex++) {
                bookRatingRepository.save(new BookRating(users.get(userIndex), book, ratings[bookIndex][userIndex]));
            }
        }
    }

    private void ensureComments(List<UserAccount> users) {
        if (bookCommentRepository.count() > 0 || users.isEmpty() || bookRepository.count() == 0) {
            return;
        }

        List<Book> books = bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        if (books.size() < 3) {
            return;
        }

        bookCommentRepository.saveAll(List.of(
                new BookComment(users.get(0), books.get(0), "Clear explanations and practical examples."),
                new BookComment(users.get(1), books.get(1), "A must-read reference for Java developers."),
                new BookComment(users.get(2), books.get(2), "Excellent systems thinking throughout the book.")
        ));
    }

    private void refreshAverageRatings() {
        List<Book> books = bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        boolean changed = false;
        for (Book book : books) {
            Double average = bookRatingRepository.findAverageRatingByBookId(book.getId());
            BigDecimal averageRating = average == null
                    ? ZERO_RATING
                    : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
            if (book.getAverageRating() == null || book.getAverageRating().compareTo(averageRating) != 0) {
                book.setAverageRating(averageRating);
                changed = true;
            }
        }

        if (changed) {
            bookRepository.saveAll(books);
        }
    }

    private List<Author> defaultAuthors() {
        return List.of(
                new Author("Robert", "Martin", "Software craftsman and author focused on clean code practices.", "Prentice Hall"),
                new Author("Joshua", "Bloch", "Java language expert known for API design and best practices.", "Addison-Wesley"),
                new Author("Martin", "Kleppmann", "Engineer and author focused on distributed systems and data architecture.", "O'Reilly Media"),
                new Author("Andrew", "Hunt", "Co-author of pragmatic software development classics.", "Addison-Wesley"),
                new Author("Craig", "Walls", "Spring ecosystem educator and author.", "Manning"),
                new Author("Martin", "Fowler", "Software design author focused on refactoring and architecture.", "Addison-Wesley"),
                new Author("Aditya", "Bhargava", "Author known for accessible algorithm explanations.", "Manning"),
                new Author("Thomas", "Cormen", "Co-author of a foundational algorithms textbook.", "MIT Press"),
                new Author("Sam", "Newman", "Author and consultant focused on microservice architecture.", "O'Reilly Media"),
                new Author("Alex", "Petrov", "Engineer and author focused on database internals.", "O'Reilly Media")
        );
    }

    private List<Book> defaultBooks(Map<String, Author> authorsByName) {
        return List.of(
                new Book(
                        "9780132350884",
                        "Clean Code",
                        "A handbook of agile software craftsmanship with practical clean code examples.",
                        authorsByName.get("Robert Martin"),
                        "Software Engineering",
                        "Prentice Hall",
                        2008,
                        new BigDecimal("39.99"),
                        8500,
                        ZERO_RATING
                ),
                new Book(
                        "9780134685991",
                        "Effective Java",
                        "Best practices for writing robust and maintainable Java applications.",
                        authorsByName.get("Joshua Bloch"),
                        "Java",
                        "Addison-Wesley",
                        2018,
                        new BigDecimal("45.00"),
                        9200,
                        ZERO_RATING
                ),
                new Book(
                        "9781449373320",
                        "Designing Data-Intensive Applications",
                        "A deep dive into reliable, scalable, and maintainable data systems.",
                        authorsByName.get("Martin Kleppmann"),
                        "Data Engineering",
                        "O'Reilly Media",
                        2017,
                        new BigDecimal("54.50"),
                        6400,
                        ZERO_RATING
                ),
                new Book(
                        "9780135957059",
                        "The Pragmatic Programmer",
                        "Career-long engineering lessons for pragmatic software delivery.",
                        authorsByName.get("Andrew Hunt"),
                        "Software Engineering",
                        "Addison-Wesley",
                        2019,
                        new BigDecimal("42.75"),
                        7800,
                        ZERO_RATING
                ),
                new Book(
                        "9781617297571",
                        "Spring in Action",
                        "Hands-on guidance for building Spring applications end to end.",
                        authorsByName.get("Craig Walls"),
                        "Java",
                        "Manning",
                        2022,
                        new BigDecimal("49.99"),
                        5100,
                        ZERO_RATING
                ),
                new Book(
                        "9780134757599",
                        "Refactoring",
                        "A catalog of proven techniques for improving existing code safely.",
                        authorsByName.get("Martin Fowler"),
                        "Software Engineering",
                        "Addison-Wesley",
                        2018,
                        new BigDecimal("47.20"),
                        6700,
                        ZERO_RATING
                ),
                new Book(
                        "9781617292231",
                        "Grokking Algorithms",
                        "A visual introduction to common algorithms and data structures.",
                        authorsByName.get("Aditya Bhargava"),
                        "Algorithms",
                        "Manning",
                        2016,
                        new BigDecimal("35.95"),
                        5900,
                        ZERO_RATING
                ),
                new Book(
                        "9780262046305",
                        "Introduction to Algorithms",
                        "Comprehensive coverage of classical and modern algorithm design.",
                        authorsByName.get("Thomas Cormen"),
                        "Algorithms",
                        "MIT Press",
                        2022,
                        new BigDecimal("89.99"),
                        4300,
                        ZERO_RATING
                ),
                new Book(
                        "9781492034025",
                        "Building Microservices",
                        "Patterns and tradeoffs for designing and operating microservice systems.",
                        authorsByName.get("Sam Newman"),
                        "Architecture",
                        "O'Reilly Media",
                        2021,
                        new BigDecimal("44.90"),
                        3600,
                        ZERO_RATING
                ),
                new Book(
                        "9781492040347",
                        "Database Internals",
                        "A systems-level look at storage engines, indexes, and replication.",
                        authorsByName.get("Alex Petrov"),
                        "Databases",
                        "O'Reilly Media",
                        2019,
                        new BigDecimal("58.40"),
                        2800,
                        ZERO_RATING
                )
        );
    }

    private void backfillExistingBooks(Map<String, Author> authorsByName) {
        List<Book> existingBooks = bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        Map<String, Book> seedBooksByTitle = defaultBooks(authorsByName).stream()
                .collect(Collectors.toMap(Book::getTitle, Function.identity()));

        boolean changed = false;
        for (Book book : existingBooks) {
            Book seedBook = seedBooksByTitle.get(book.getTitle());
            if (seedBook == null) {
                continue;
            }

            if (book.getIsbn() == null || book.getIsbn().isBlank()) {
                book.setIsbn(seedBook.getIsbn());
                changed = true;
            }
            if (book.getDescription() == null || book.getDescription().isBlank()) {
                book.setDescription(seedBook.getDescription());
                changed = true;
            }
            if (book.getAuthor() == null) {
                book.setAuthor(seedBook.getAuthor());
                changed = true;
            }
            if (book.getPublisher() == null || book.getPublisher().isBlank()) {
                book.setPublisher(seedBook.getPublisher());
                changed = true;
            }
            if (book.getYearPublished() == null) {
                book.setYearPublished(seedBook.getYearPublished());
                changed = true;
            }
            if (book.getAverageRating() == null) {
                book.setAverageRating(ZERO_RATING);
                changed = true;
            }
        }

        if (changed) {
            bookRepository.saveAll(existingBooks);
        }
    }
}
