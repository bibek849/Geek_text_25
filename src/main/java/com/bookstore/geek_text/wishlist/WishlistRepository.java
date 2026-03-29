package com.bookstore.geek_text.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}
