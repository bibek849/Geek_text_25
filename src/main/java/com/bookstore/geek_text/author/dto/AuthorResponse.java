package com.bookstore.geek_text.author.dto;

import com.bookstore.geek_text.author.Author;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        String biography,
        String publisher
) {

    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBiography(),
                author.getPublisher()
        );
    }
}
