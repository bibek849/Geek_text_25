package com.bookstore.geek_text.book.dto;

import com.bookstore.geek_text.book.Book;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String description,
        Long authorId,
        String author,
        String genre,
        String publisher,
        Integer yearPublished,
        BigDecimal price,
        Integer copiesSold,
        BigDecimal averageRating
) {

    public static BookResponse from(Book book) {
        Long authorId = book.getAuthor() == null ? null : book.getAuthor().getId();
        String authorName = book.getAuthor() == null ? null : book.getAuthor().getFullName();
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getDescription(),
                authorId,
                authorName,
                book.getGenre(),
                book.getPublisher(),
                book.getYearPublished(),
                book.getPrice(),
                book.getCopiesSold(),
                book.getAverageRating()
        );
    }
}
