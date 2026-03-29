package com.bookstore.geek_text.review.dto;

import java.math.BigDecimal;

public record AverageRatingResponse(
        Long bookId,
        BigDecimal averageRating
) {
}
