package com.bookstore.geek_text.profile.dto;

import com.bookstore.geek_text.profile.CreditCard;

public record CreditCardResponse(
        Long id,
        String nameOnCard,
        String maskedCardNumber,
        Integer expirationMonth,
        Integer expirationYear
) {

    public static CreditCardResponse from(CreditCard creditCard) {
        String cardNumber = creditCard.getCardNumber();
        String lastFourDigits = cardNumber.length() <= 4 ? cardNumber : cardNumber.substring(cardNumber.length() - 4);
        return new CreditCardResponse(
                creditCard.getId(),
                creditCard.getNameOnCard(),
                "****" + lastFourDigits,
                creditCard.getExpirationMonth(),
                creditCard.getExpirationYear()
        );
    }
}
