package com.bookstore.geek_text.wishlist;

import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.wishlist.dto.CreateWishlistRequest;
import com.bookstore.geek_text.wishlist.dto.WishlistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WishlistService wishlistService;

    @Test
    void createWishlistReturnsCreatedWishlist() throws Exception {
        CreateWishlistRequest request = new CreateWishlistRequest("Favorites");
        WishlistResponse response = new WishlistResponse(5L, 1L, "Favorites", List.of());
        given(wishlistService.createWishlist(1L, request)).willReturn(response);

        mockMvc.perform(post("/api/users/1/wishlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Favorites"));
    }

    @Test
    void getWishlistBooksReturnsBookList() throws Exception {
        BookResponse book = new BookResponse(
                1L,
                "9780132350884",
                "Clean Code",
                "A handbook of agile software craftsmanship.",
                3L,
                "Robert Martin",
                "Software Engineering",
                "Prentice Hall",
                2008,
                new BigDecimal("39.99"),
                8500,
                new BigDecimal("4.67")
        );
        given(wishlistService.getWishlistBooks(5L)).willReturn(List.of(book));

        mockMvc.perform(get("/api/wishlists/5/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    void moveBookToCartReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/wishlists/5/books/1/move-to-cart"))
                .andExpect(status().isNoContent());

        verify(wishlistService).moveBookToCart(5L, 1L);
    }
}
