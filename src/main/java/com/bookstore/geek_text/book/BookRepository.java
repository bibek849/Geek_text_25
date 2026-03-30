package com.bookstore.geek_text.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbnIgnoreCase(String isbn);

    List<Book> findByGenreIgnoreCaseOrderByTitleAsc(String genre);

    List<Book> findTop10ByOrderByCopiesSoldDescTitleAsc();

    List<Book> findByAverageRatingGreaterThanEqualOrderByAverageRatingDescTitleAsc(BigDecimal minRating);

    List<Book> findByPublisherIgnoreCaseOrderByIdAsc(String publisher);

    List<Book> findByAuthorIdOrderByTitleAsc(Long authorId);
}
