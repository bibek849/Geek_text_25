package com.bookstore.geek_text.review;

import com.bookstore.geek_text.review.dto.AverageRatingResponse;
import com.bookstore.geek_text.review.dto.CommentResponse;
import com.bookstore.geek_text.review.dto.CreateCommentRequest;
import com.bookstore.geek_text.review.dto.CreateRatingRequest;
import com.bookstore.geek_text.review.dto.RatingResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping({"/api/books", "/books"})
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{bookId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponse createRating(
            @PathVariable @Positive Long bookId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        return reviewService.createRating(bookId, request);
    }

    @GetMapping("/{bookId}/ratings/average")
    public AverageRatingResponse getAverageRating(@PathVariable @Positive Long bookId) {
        return reviewService.getAverageRating(bookId);
    }

    @PostMapping("/{bookId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @PathVariable @Positive Long bookId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return reviewService.createComment(bookId, request);
    }

    @GetMapping("/{bookId}/comments")
    public List<CommentResponse> getComments(@PathVariable @Positive Long bookId) {
        return reviewService.getCommentsByBookId(bookId);
    }
}
