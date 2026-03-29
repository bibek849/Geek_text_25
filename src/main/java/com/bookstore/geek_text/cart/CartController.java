package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.cart.dto.AddToCartRequest;
import com.bookstore.geek_text.cart.dto.CartItemResponse;
import com.bookstore.geek_text.cart.dto.CartSubtotalResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping({"/api/cart", "/cart"})
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addToCart(@Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    @GetMapping("/{userId}")
    public List<CartItemResponse> getCart(@PathVariable @Positive Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @GetMapping("/{userId}/subtotal")
    public CartSubtotalResponse getCartSubtotal(@PathVariable @Positive Long userId) {
        return cartService.getCartSubtotal(userId);
    }

    @DeleteMapping("/{userId}/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromCart(@PathVariable @Positive Long userId, @PathVariable @Positive Long bookId) {
        cartService.removeBookFromCart(userId, bookId);
    }
}
