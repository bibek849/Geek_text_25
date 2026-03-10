package com.bookstore.geek_text.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long cartItemId,
        Long userId,
        Long bookId,
        String isbn,
        String title,
        String author,
        String genre,
        BigDecimal price,
        Integer quantity
) {
}
