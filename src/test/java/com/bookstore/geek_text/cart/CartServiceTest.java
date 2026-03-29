package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.author.Author;
import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookRepository;
import com.bookstore.geek_text.cart.dto.AddToCartRequest;
import com.bookstore.geek_text.cart.dto.CartItemResponse;
import com.bookstore.geek_text.cart.dto.CartSubtotalResponse;
import com.bookstore.geek_text.profile.UserAccount;
import com.bookstore.geek_text.profile.UserAccountRepository;
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

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCartCreatesNewItemWhenBookNotInCart() {
        UserAccount user = buildUser(10L, "reader1");
        Book book = buildBook(7L, "9780262046305", "Introduction to Algorithms");
        given(userAccountRepository.findById(10L)).willReturn(Optional.of(user));
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
    }

    @Test
    void addToCartIncrementsExistingItem() {
        UserAccount user = buildUser(5L, "reader2");
        Book book = buildBook(3L, "9780135957059", "The Pragmatic Programmer");
        CartItem existing = new CartItem(user, book, 1);
        ReflectionTestUtils.setField(existing, "id", 22L);

        given(userAccountRepository.findById(5L)).willReturn(Optional.of(user));
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
        UserAccount user = buildUser(11L, "reader3");
        Book book = buildBook(2L, "9780134685991", "Effective Java");
        given(userAccountRepository.findById(11L)).willReturn(Optional.of(user));
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
        UserAccount user = buildUser(1L, "reader4");
        given(userAccountRepository.findById(1L)).willReturn(Optional.of(user));
        given(bookRepository.findById(999L)).willReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> cartService.addToCart(new AddToCartRequest(1L, 999L, 1))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getCartSubtotalReturnsComputedAmount() {
        UserAccount user = buildUser(3L, "reader5");
        Book firstBook = buildBook(8L, "9781492040347", "Database Internals");
        Book secondBook = buildBook(9L, "9781492034025", "Building Microservices");
        CartItem firstItem = new CartItem(user, firstBook, 2);
        CartItem secondItem = new CartItem(user, secondBook, 1);
        given(userAccountRepository.existsById(3L)).willReturn(true);
        given(cartItemRepository.findByUserIdOrderByIdAsc(3L)).willReturn(List.of(firstItem, secondItem));

        CartSubtotalResponse result = cartService.getCartSubtotal(3L);

        assertEquals(3L, result.userId());
        assertEquals(3, result.totalQuantity());
        assertEquals(new BigDecimal("89.97"), result.subtotal());
    }

    @Test
    void removeBookFromCartDeletesItem() {
        UserAccount user = buildUser(4L, "reader6");
        Book book = buildBook(1L, "9780132350884", "Clean Code");
        CartItem existing = new CartItem(user, book, 1);
        given(userAccountRepository.existsById(4L)).willReturn(true);
        given(cartItemRepository.findByUserIdAndBookId(4L, 1L)).willReturn(Optional.of(existing));

        cartService.removeBookFromCart(4L, 1L);

        verify(cartItemRepository).delete(existing);
    }

    private UserAccount buildUser(Long id, String username) {
        UserAccount user = new UserAccount(username, "Password123!", "Reader", username + "@example.com", "123 Main St");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Book buildBook(Long id, String isbn, String title) {
        Author author = new Author("Author", "Example", "Bio", "Publisher");
        ReflectionTestUtils.setField(author, "id", 12L);
        Book book = new Book(
                isbn,
                title,
                "Description",
                author,
                "Genre",
                "Publisher",
                2024,
                new BigDecimal("29.99"),
                2500,
                new BigDecimal("4.50")
        );
        ReflectionTestUtils.setField(book, "id", id);
        return book;
    }
}
