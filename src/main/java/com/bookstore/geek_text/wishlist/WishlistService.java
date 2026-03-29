package com.bookstore.geek_text.wishlist;

import com.bookstore.geek_text.book.Book;
import com.bookstore.geek_text.book.BookService;
import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.cart.CartService;
import com.bookstore.geek_text.profile.UserAccount;
import com.bookstore.geek_text.profile.UserService;
import com.bookstore.geek_text.wishlist.dto.CreateWishlistRequest;
import com.bookstore.geek_text.wishlist.dto.WishlistResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserService userService;
    private final BookService bookService;
    private final CartService cartService;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            UserService userService,
            BookService bookService,
            CartService cartService
    ) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.cartService = cartService;
    }

    public WishlistResponse createWishlist(Long userId, CreateWishlistRequest request) {
        if (wishlistRepository.existsByUserIdAndNameIgnoreCase(userId, request.name())) {
            throw new ResponseStatusException(CONFLICT, "Wishlist already exists for user " + userId + ": " + request.name());
        }

        UserAccount user = userService.getUserById(userId);
        Wishlist saved = wishlistRepository.save(new Wishlist(user, request.name()));
        return toResponse(saved);
    }

    public WishlistResponse addBookToWishlist(Long wishlistId, Long bookId) {
        Wishlist wishlist = getWishlistById(wishlistId);
        Book book = bookService.findBookById(bookId);
        wishlistItemRepository.findByWishlistIdAndBookId(wishlistId, bookId)
                .orElseGet(() -> wishlistItemRepository.save(new WishlistItem(wishlist, book)));
        return toResponse(wishlist);
    }

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(Long wishlistId) {
        return toResponse(getWishlistById(wishlistId));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getWishlistBooks(Long wishlistId) {
        getWishlistById(wishlistId);
        return wishlistItemRepository.findByWishlistIdOrderByIdAsc(wishlistId).stream()
                .map(WishlistItem::getBook)
                .map(BookResponse::from)
                .toList();
    }

    public void moveBookToCart(Long wishlistId, Long bookId) {
        Wishlist wishlist = getWishlistById(wishlistId);
        WishlistItem item = wishlistItemRepository.findByWishlistIdAndBookId(wishlistId, bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Wishlist item not found for wishlist " + wishlistId + " and book " + bookId));

        cartService.addToCart(wishlist.getUser().getId(), bookId, 1);
        wishlistItemRepository.delete(item);
    }

    private Wishlist getWishlistById(Long wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Wishlist not found for id: " + wishlistId));
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getUser().getId(),
                wishlist.getName(),
                getWishlistBooks(wishlist.getId())
        );
    }
}
