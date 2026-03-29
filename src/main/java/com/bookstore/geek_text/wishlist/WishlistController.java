package com.bookstore.geek_text.wishlist;

import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.wishlist.dto.CreateWishlistRequest;
import com.bookstore.geek_text.wishlist.dto.WishlistResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping({"/api/users/{userId}/wishlists", "/users/{userId}/wishlists"})
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistResponse createWishlist(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody CreateWishlistRequest request
    ) {
        return wishlistService.createWishlist(userId, request);
    }

    @PostMapping({"/api/wishlists/{wishlistId}/books/{bookId}", "/wishlists/{wishlistId}/books/{bookId}"})
    public WishlistResponse addBookToWishlist(
            @PathVariable @Positive Long wishlistId,
            @PathVariable @Positive Long bookId
    ) {
        return wishlistService.addBookToWishlist(wishlistId, bookId);
    }

    @GetMapping({"/api/wishlists/{wishlistId}", "/wishlists/{wishlistId}"})
    public WishlistResponse getWishlist(@PathVariable @Positive Long wishlistId) {
        return wishlistService.getWishlist(wishlistId);
    }

    @GetMapping({"/api/wishlists/{wishlistId}/books", "/wishlists/{wishlistId}/books"})
    public List<BookResponse> getWishlistBooks(@PathVariable @Positive Long wishlistId) {
        return wishlistService.getWishlistBooks(wishlistId);
    }

    @DeleteMapping({"/api/wishlists/{wishlistId}/books/{bookId}/move-to-cart", "/wishlists/{wishlistId}/books/{bookId}/move-to-cart"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moveBookToCart(
            @PathVariable @Positive Long wishlistId,
            @PathVariable @Positive Long bookId
    ) {
        wishlistService.moveBookToCart(wishlistId, bookId);
    }
}
