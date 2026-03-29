package com.bookstore.geek_text.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCreditCardRequest(
        @NotBlank(message = "cardNumber is required")
        String cardNumber,

        @NotBlank(message = "nameOnCard is required")
        String nameOnCard,

        @NotNull(message = "expirationMonth is required")
        @Min(value = 1, message = "expirationMonth must be between 1 and 12")
        @Max(value = 12, message = "expirationMonth must be between 1 and 12")
        Integer expirationMonth,

        @NotNull(message = "expirationYear is required")
        @Min(value = 2024, message = "expirationYear must be valid")
        Integer expirationYear
) {
}
