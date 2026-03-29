package com.bookstore.geek_text.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "password is required")
        String password,

        String name,

        @Email(message = "email must be valid")
        String email,

        String homeAddress
) {
}
