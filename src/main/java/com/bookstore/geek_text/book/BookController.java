package com.bookstore.geek_text.book;

import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.book.dto.CreateBookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@Validated
@RequestMapping({"/api/books", "/books"})
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> getBooks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal minRating
    ) {
        return bookService.getBooks(genre, minRating);
    }

    @GetMapping("/top-sellers")
    public List<BookResponse> getTopSellers() {
        return bookService.getTopSellers();
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable @Positive Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponse getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public BookResponse createBook(@Valid @RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @PatchMapping("/discount")
    @ResponseStatus(NO_CONTENT)
    public void discountBooksByPublisher(
            @RequestParam String publisher,
            @RequestParam @DecimalMin("0.01") @DecimalMax("100.0") BigDecimal percent
    ) {
        bookService.discountBooksByPublisher(publisher, percent);
    }
}
