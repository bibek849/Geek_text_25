package com.bookstore.geek_text.profile;

import com.bookstore.geek_text.profile.dto.CreateCreditCardRequest;
import com.bookstore.geek_text.profile.dto.CreateUserRequest;
import com.bookstore.geek_text.profile.dto.CreditCardResponse;
import com.bookstore.geek_text.profile.dto.UpdateUserRequest;
import com.bookstore.geek_text.profile.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/api/users", "/users"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{username}")
    public UserResponse getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PatchMapping("/{username}")
    public UserResponse updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(username, request);
    }

    @PostMapping("/{username}/credit-cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardResponse addCreditCard(
            @PathVariable String username,
            @Valid @RequestBody CreateCreditCardRequest request
    ) {
        return userService.addCreditCard(username, request);
    }
}
