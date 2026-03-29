package com.bookstore.geek_text.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRatingRepository extends JpaRepository<BookRating, Long> {

    Optional<BookRating> findByUserIdAndBookId(Long userId, Long bookId);

    @Query("select avg(r.rating) from BookRating r where r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}
