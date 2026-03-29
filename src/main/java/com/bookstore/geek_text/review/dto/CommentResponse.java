package com.bookstore.geek_text.review.dto;

import com.bookstore.geek_text.review.BookComment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long bookId,
        Long userId,
        String username,
        String comment,
        LocalDateTime createdAt
) {

    public static CommentResponse from(BookComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBook().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }
}
