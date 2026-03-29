package com.bookstore.geek_text.profile.dto;

public record UpdateUserRequest(
        String password,
        String name,
        String homeAddress
) {
}
