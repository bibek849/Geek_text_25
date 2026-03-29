package com.bookstore.geek_text.profile.dto;

import com.bookstore.geek_text.profile.UserAccount;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String name,
        String email,
        String homeAddress,
        List<CreditCardResponse> creditCards
) {

    public static UserResponse from(UserAccount user, List<CreditCardResponse> creditCards) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getHomeAddress(),
                creditCards
        );
    }
}
