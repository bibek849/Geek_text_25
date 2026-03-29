package com.bookstore.geek_text.wishlist.dto;

import com.bookstore.geek_text.book.dto.BookResponse;

import java.util.List;

public record WishlistResponse(
        Long id,
        Long userId,
        String name,
        List<BookResponse> books
) {
}
