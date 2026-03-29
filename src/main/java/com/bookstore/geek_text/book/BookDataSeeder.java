package com.bookstore.geek_text.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BookDataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;

    public BookDataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.count() > 0) {
            return;
        }

        List<Book> sampleBooks = List.of(
                new Book("Clean Code", "Robert C. Martin", "Software Engineering", new BigDecimal("39.99"), 8500),
                new Book("Effective Java", "Joshua Bloch", "Java", new BigDecimal("45.00"), 9200),
                new Book("Designing Data-Intensive Applications", "Martin Kleppmann", "Data Engineering", new BigDecimal("54.50"), 6400),
                new Book("The Pragmatic Programmer", "Andrew Hunt", "Software Engineering", new BigDecimal("42.75"), 7800),
                new Book("Spring in Action", "Craig Walls", "Java", new BigDecimal("49.99"), 5100),
                new Book("Refactoring", "Martin Fowler", "Software Engineering", new BigDecimal("47.20"), 6700),
                new Book("Grokking Algorithms", "Aditya Bhargava", "Algorithms", new BigDecimal("35.95"), 5900),
                new Book("Introduction to Algorithms", "Thomas H. Cormen", "Algorithms", new BigDecimal("89.99"), 4300),
                new Book("Building Microservices", "Sam Newman", "Architecture", new BigDecimal("44.90"), 3600),
                new Book("Database Internals", "Alex Petrov", "Databases", new BigDecimal("58.40"), 2800)
        );

        bookRepository.saveAll(sampleBooks);
    }
}
