package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookRepository;
import com.bookstore.geek_text.cart.dto.AddToCartRequest;
import com.bookstore.geek_text.cart.dto.CartItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCartCreatesNewItemWhenBookNotInCart() {
        Book book = buildBook(7L, "9780262046305", "Introduction to Algorithms");
        given(bookRepository.findById(7L)).willReturn(Optional.of(book));
        given(cartItemRepository.findByUserIdAndBookId(10L, 7L)).willReturn(Optional.empty());
        given(cartItemRepository.save(any(CartItem.class))).willAnswer(invocation -> {
            CartItem created = invocation.getArgument(0);
            ReflectionTestUtils.setField(created, "id", 101L);
            return created;
        });

        CartItemResponse response = cartService.addToCart(new AddToCartRequest(10L, 7L, 2));

        assertEquals(101L, response.cartItemId());
        assertEquals(10L, response.userId());
        assertEquals(7L, response.bookId());
        assertEquals(2, response.quantity());
        assertEquals("9780262046305", response.isbn());
    }

    @Test
    void addToCartIncrementsExistingItem() {
        Book book = buildBook(3L, "9780135957059", "The Pragmatic Programmer");
        CartItem existing = new CartItem(5L, book, 1);
        ReflectionTestUtils.setField(existing, "id", 22L);

        given(bookRepository.findById(3L)).willReturn(Optional.of(book));
        given(cartItemRepository.findByUserIdAndBookId(5L, 3L)).willReturn(Optional.of(existing));
        given(cartItemRepository.save(existing)).willReturn(existing);

        CartItemResponse response = cartService.addToCart(new AddToCartRequest(5L, 3L, 3));

        assertEquals(4, existing.getQuantity());
        assertEquals(4, response.quantity());
        assertEquals(22L, response.cartItemId());
    }

    @Test
    void addToCartDefaultsQuantityToOneWhenMissing() {
        Book book = buildBook(2L, "9780134685991", "Effective Java");
        given(bookRepository.findById(2L)).willReturn(Optional.of(book));
        given(cartItemRepository.findByUserIdAndBookId(11L, 2L)).willReturn(Optional.empty());
        given(cartItemRepository.save(any(CartItem.class))).willAnswer(invocation -> invocation.getArgument(0));

        cartService.addToCart(new AddToCartRequest(11L, 2L, null));

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getQuantity());
    }

    @Test
    void addToCartThrows404WhenBookDoesNotExist() {
        given(bookRepository.findById(999L)).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> cartService.addToCart(new AddToCartRequest(1L, 999L, 1))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getCartByUserIdReturnsMappedResponse() {
        Book book = buildBook(8L, "9781492040347", "Database Internals");
        CartItem cartItem = new CartItem(3L, book, 5);
        ReflectionTestUtils.setField(cartItem, "id", 600L);
        given(cartItemRepository.findByUserIdOrderByIdAsc(3L)).willReturn(List.of(cartItem));

        List<CartItemResponse> result = cartService.getCartByUserId(3L);

        assertEquals(1, result.size());
        assertEquals(600L, result.getFirst().cartItemId());
        assertEquals("Database Internals", result.getFirst().title());
        assertEquals(5, result.getFirst().quantity());
    }

    private Book buildBook(Long id, String isbn, String title) {
        Book book = new Book(isbn, title, "Author", "Genre", new BigDecimal("29.99"), 2500);
        ReflectionTestUtils.setField(book, "id", id);
        return book;
    }
}
