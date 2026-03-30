package com.bookstore.geek_text.author.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAuthorRequest(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @NotBlank(message = "biography is required")
        String biography,

        @NotBlank(message = "publisher is required")
        String publisher
) {
}
