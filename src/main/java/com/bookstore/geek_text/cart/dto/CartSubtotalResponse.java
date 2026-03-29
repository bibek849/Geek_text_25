package com.bookstore.geek_text.cart.dto;

import java.math.BigDecimal;

public record CartSubtotalResponse(
        Long userId,
        Integer totalQuantity,
        BigDecimal subtotal
) {
}
