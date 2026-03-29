package com.bookstore.geek_text.review;

import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookRepository;
import com.bookstore.geek_text.book.BookService;
import com.bookstore.geek_text.profile.UserAccount;
import com.bookstore.geek_text.profile.UserService;
import com.bookstore.geek_text.review.dto.AverageRatingResponse;
import com.bookstore.geek_text.review.dto.CommentResponse;
import com.bookstore.geek_text.review.dto.CreateCommentRequest;
import com.bookstore.geek_text.review.dto.CreateRatingRequest;
import com.bookstore.geek_text.review.dto.RatingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final BookRatingRepository bookRatingRepository;
    private final BookCommentRepository bookCommentRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final UserService userService;

    public ReviewService(
            BookRatingRepository bookRatingRepository,
            BookCommentRepository bookCommentRepository,
            BookRepository bookRepository,
            BookService bookService,
            UserService userService
    ) {
        this.bookRatingRepository = bookRatingRepository;
        this.bookCommentRepository = bookCommentRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.userService = userService;
    }

    public RatingResponse createRating(Long bookId, CreateRatingRequest request) {
        UserAccount user = userService.getUserById(request.userId());
        Book book = bookService.findBookById(bookId);

        BookRating rating = bookRatingRepository.findByUserIdAndBookId(request.userId(), bookId)
                .map(existing -> {
                    existing.setRating(request.rating());
                    return existing;
                })
                .orElseGet(() -> new BookRating(user, book, request.rating()));

        BookRating saved = bookRatingRepository.save(rating);
        updateBookAverageRating(book);
        return RatingResponse.from(saved);
    }

    public CommentResponse createComment(Long bookId, CreateCommentRequest request) {
        UserAccount user = userService.getUserById(request.userId());
        Book book = bookService.findBookById(bookId);
        BookComment saved = bookCommentRepository.save(new BookComment(user, book, request.comment()));
        return CommentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByBookId(Long bookId) {
        bookService.findBookById(bookId);
        return bookCommentRepository.findByBookIdOrderByCreatedAtDescIdDesc(bookId).stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AverageRatingResponse getAverageRating(Long bookId) {
        Book book = bookService.findBookById(bookId);
        BigDecimal averageRating = book.getAverageRating() == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : book.getAverageRating();
        return new AverageRatingResponse(bookId, averageRating);
    }

    private void updateBookAverageRating(Book book) {
        Double average = bookRatingRepository.findAverageRatingByBookId(book.getId());
        BigDecimal averageRating = average == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
        book.setAverageRating(averageRating);
        bookRepository.save(book);
    }
}
