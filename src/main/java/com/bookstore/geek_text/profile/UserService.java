package com.bookstore.geek_text.profile;

import com.bookstore.geek_text.profile.dto.CreateCreditCardRequest;
import com.bookstore.geek_text.profile.dto.CreateUserRequest;
import com.bookstore.geek_text.profile.dto.CreditCardResponse;
import com.bookstore.geek_text.profile.dto.UpdateUserRequest;
import com.bookstore.geek_text.profile.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final CreditCardRepository creditCardRepository;

    public UserService(UserAccountRepository userAccountRepository, CreditCardRepository creditCardRepository) {
        this.userAccountRepository = userAccountRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userAccountRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ResponseStatusException(CONFLICT, "Username already exists: " + request.username());
        }

        UserAccount user = new UserAccount(
                request.username(),
                request.password(),
                request.name(),
                request.email(),
                request.homeAddress()
        );

        return toUserResponse(userAccountRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        return toUserResponse(findUserByUsername(username));
    }

    public UserResponse updateUser(String username, UpdateUserRequest request) {
        UserAccount user = findUserByUsername(username);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(request.password());
        }
        if (request.name() != null) {
            user.setName(request.name());
        }
        if (request.homeAddress() != null) {
            user.setHomeAddress(request.homeAddress());
        }

        return toUserResponse(userAccountRepository.save(user));
    }

    public CreditCardResponse addCreditCard(String username, CreateCreditCardRequest request) {
        UserAccount user = findUserByUsername(username);
        CreditCard creditCard = new CreditCard(
                user,
                request.cardNumber(),
                request.nameOnCard(),
                request.expirationMonth(),
                request.expirationYear()
        );
        return CreditCardResponse.from(creditCardRepository.save(creditCard));
    }

    @Transactional(readOnly = true)
    public UserAccount getUserById(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found for id: " + userId));
    }

    @Transactional(readOnly = true)
    public UserAccount findUserByUsername(String username) {
        return userAccountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found for username: " + username));
    }

    private UserResponse toUserResponse(UserAccount user) {
        List<CreditCardResponse> cards = creditCardRepository.findByUserIdOrderByIdAsc(user.getId()).stream()
                .map(CreditCardResponse::from)
                .toList();
        return UserResponse.from(user, cards);
    }
}
