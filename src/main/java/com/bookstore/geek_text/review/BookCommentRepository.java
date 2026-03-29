package com.bookstore.geek_text.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCommentRepository extends JpaRepository<BookComment, Long> {

    List<BookComment> findByBookIdOrderByCreatedAtDescIdDesc(Long bookId);
}
