package com.bookstore.geek_text.book;

import com.bookstore.geek_text.author.Author;
import com.bookstore.geek_text.author.AuthorRepository;
import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.book.dto.CreateBookRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooks(String genre, BigDecimal minRating) {
        if (genre != null && !genre.isBlank()) {
            return bookRepository.findByGenreIgnoreCaseOrderByTitleAsc(genre).stream()
                    .map(BookResponse::from)
                    .toList();
        }

        if (minRating != null) {
            return bookRepository.findByAverageRatingGreaterThanEqualOrderByAverageRatingDescTitleAsc(minRating).stream()
                    .map(BookResponse::from)
                    .toList();
        }

        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getTopSellers() {
        return bookRepository.findTop10ByOrderByCopiesSoldDescTitleAsc().stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return BookResponse.from(findBookById(id));
    }

    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {
        return BookResponse.from(bookRepository.findByIsbnIgnoreCase(isbn)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for isbn: " + isbn)));
    }

    public BookResponse createBook(CreateBookRequest request) {
        bookRepository.findByIsbnIgnoreCase(request.isbn()).ifPresent(existing -> {
            throw new ResponseStatusException(CONFLICT, "Book already exists for isbn: " + request.isbn());
        });

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Author not found for id: " + request.authorId()));

        Book book = new Book(
                request.isbn(),
                request.title(),
                request.description(),
                author,
                request.genre(),
                request.publisher(),
                request.yearPublished(),
                request.price(),
                request.copiesSold(),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        );

        return BookResponse.from(bookRepository.save(book));
    }

    public void discountBooksByPublisher(String publisher, BigDecimal percent) {
        List<Book> books = bookRepository.findByPublisherIgnoreCaseOrderByIdAsc(publisher);
        if (books.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND, "No books found for publisher: " + publisher);
        }

        BigDecimal multiplier = BigDecimal.ONE.subtract(percent.movePointLeft(2));
        for (Book book : books) {
            book.setPrice(book.getPrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
        }
        bookRepository.saveAll(books);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorIdOrderByTitleAsc(authorId).stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));
    }
}
