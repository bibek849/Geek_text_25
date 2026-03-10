package com.bookstore.geek_text.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndBookId(Long userId, Long bookId);

    List<CartItem> findByUserIdOrderByIdAsc(Long userId);
}
