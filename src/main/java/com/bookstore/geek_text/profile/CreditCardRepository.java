package com.bookstore.geek_text.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findByUserIdOrderByIdAsc(Long userId);
}
