package com.bookstore.geek_text.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToCartRequest(
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be a positive number")
        Long userId,

        @NotNull(message = "bookId is required")
        @Positive(message = "bookId must be a positive number")
        Long bookId,

        @Positive(message = "quantity must be a positive number")
        Integer quantity
) {
}
