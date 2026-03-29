package com.bookstore.geek_text.review.dto;

import com.bookstore.geek_text.review.BookRating;

import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long bookId,
        Long userId,
        String username,
        Integer rating,
        LocalDateTime createdAt
) {

    public static RatingResponse from(BookRating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getBook().getId(),
                rating.getUser().getId(),
                rating.getUser().getUsername(),
                rating.getRating(),
                rating.getCreatedAt()
        );
    }
}
