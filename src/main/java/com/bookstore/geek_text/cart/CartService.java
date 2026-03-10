package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookRepository;
import com.bookstore.geek_text.cart.dto.AddToCartRequest;
import com.bookstore.geek_text.cart.dto.CartItemResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartService(CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    public CartItemResponse addToCart(AddToCartRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(
                        () -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + request.bookId()));

        int quantityToAdd = request.quantity() == null ? 1 : request.quantity();

        CartItem cartItem = cartItemRepository.findByUserIdAndBookId(request.userId(), request.bookId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantityToAdd);
                    return existing;
                })
                .orElseGet(() -> new CartItem(request.userId(), book, quantityToAdd));

        CartItem saved = cartItemRepository.save(cartItem);
        return toResponse(saved);
    }

    public List<CartItemResponse> getCartByUserId(Long userId) {
        return cartItemRepository.findByUserIdOrderByIdAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CartItemResponse toResponse(CartItem cartItem) {
        Book book = cartItem.getBook();
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getUserId(),
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPrice(),
                cartItem.getQuantity());
    }
}
