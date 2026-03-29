package com.bookstore.geek_text.book.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateBookRequest(
        @NotBlank(message = "isbn is required")
        String isbn,

        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "description is required")
        String description,

        @NotNull(message = "authorId is required")
        @Positive(message = "authorId must be positive")
        Long authorId,

        @NotBlank(message = "genre is required")
        String genre,

        @NotBlank(message = "publisher is required")
        String publisher,

        @NotNull(message = "yearPublished is required")
        @Positive(message = "yearPublished must be positive")
        Integer yearPublished,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.01", message = "price must be positive")
        BigDecimal price,

        @NotNull(message = "copiesSold is required")
        @Positive(message = "copiesSold must be positive")
        Integer copiesSold
) {
}
