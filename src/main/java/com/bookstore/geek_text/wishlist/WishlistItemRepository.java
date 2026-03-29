package com.bookstore.geek_text.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlistIdOrderByIdAsc(Long wishlistId);

    Optional<WishlistItem> findByWishlistIdAndBookId(Long wishlistId, Long bookId);
}
