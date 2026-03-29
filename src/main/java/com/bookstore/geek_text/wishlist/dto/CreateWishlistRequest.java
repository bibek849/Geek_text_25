package com.bookstore.geek_text.wishlist.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWishlistRequest(
        @NotBlank(message = "name is required")
        String name
) {
}
