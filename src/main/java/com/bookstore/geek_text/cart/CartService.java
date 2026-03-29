package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookRepository;
import com.bookstore.geek_text.cart.dto.AddToCartRequest;
import com.bookstore.geek_text.cart.dto.CartItemResponse;
import com.bookstore.geek_text.cart.dto.CartSubtotalResponse;
import com.bookstore.geek_text.profile.UserAccount;
import com.bookstore.geek_text.profile.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserAccountRepository userAccountRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public CartItemResponse addToCart(AddToCartRequest request) {
        return addToCart(request.userId(), request.bookId(), request.quantity());
    }

    public CartItemResponse addToCart(Long userId, Long bookId, Integer quantity) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found for id: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + bookId));

        int quantityToAdd = quantity == null ? 1 : quantity;

        CartItem cartItem = cartItemRepository.findByUserIdAndBookId(userId, bookId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantityToAdd);
                    return existing;
                })
                .orElseGet(() -> new CartItem(user, book, quantityToAdd));

        CartItem saved = cartItemRepository.save(cartItem);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartByUserId(Long userId) {
        verifyUserExists(userId);
        return cartItemRepository.findByUserIdOrderByIdAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CartSubtotalResponse getCartSubtotal(Long userId) {
        verifyUserExists(userId);
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByIdAsc(userId);
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQuantity = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        return new CartSubtotalResponse(userId, totalQuantity, subtotal);
    }

    public void removeBookFromCart(Long userId, Long bookId) {
        verifyUserExists(userId);
        CartItem cartItem = cartItemRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Cart item not found for user " + userId + " and book " + bookId));
        cartItemRepository.delete(cartItem);
    }

    private void verifyUserExists(Long userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found for id: " + userId);
        }
    }

    private CartItemResponse toResponse(CartItem cartItem) {
        Book book = cartItem.getBook();
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getUser().getId(),
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor() == null ? null : book.getAuthor().getFullName(),
                book.getGenre(),
                book.getPrice(),
                cartItem.getQuantity());
    }
}
