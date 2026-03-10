package com.bookstore.geek_text.cart;

import com.bookstore.geek_text.cart.dto.CartItemResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @Test
    void addToCartReturnsCreatedResponse() throws Exception {
        CartItemResponse created = new CartItemResponse(
                55L,
                9L,
                4L,
                "9780135957059",
                "The Pragmatic Programmer",
                "Andrew Hunt",
                "Software Engineering",
                new BigDecimal("42.75"),
                2
        );
        given(cartService.addToCart(org.mockito.ArgumentMatchers.any())).willReturn(created);

        String payload = objectMapper.writeValueAsString(java.util.Map.of(
                "userId", 9,
                "bookId", 4,
                "quantity", 2
        ));

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartItemId").value(55))
                .andExpect(jsonPath("$.userId").value(9))
                .andExpect(jsonPath("$.bookId").value(4))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void addToCartReturnsBadRequestForMissingFields() throws Exception {
        String payload = objectMapper.writeValueAsString(java.util.Map.of("bookId", 4));

        mockMvc.perform(post("/api/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCartReturnsUserCartItems() throws Exception {
        CartItemResponse item = new CartItemResponse(
                77L,
                3L,
                1L,
                "9780132350884",
                "Clean Code",
                "Robert C. Martin",
                "Software Engineering",
                new BigDecimal("39.99"),
                1
        );
        given(cartService.getCartByUserId(3L)).willReturn(List.of(item));

        mockMvc.perform(get("/api/cart/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cartItemId").value(77))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].isbn").value("9780132350884"));
    }
}
