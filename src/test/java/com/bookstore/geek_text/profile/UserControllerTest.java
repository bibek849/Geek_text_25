package com.bookstore.geek_text.profile;

import com.bookstore.geek_text.profile.dto.CreateCreditCardRequest;
import com.bookstore.geek_text.profile.dto.CreateUserRequest;
import com.bookstore.geek_text.profile.dto.CreditCardResponse;
import com.bookstore.geek_text.profile.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUserReturnsCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("alice", "Password123!", "Alice Carter", "alice@example.com", "123 Main St");
        UserResponse response = new UserResponse(1L, "alice", "Alice Carter", "alice@example.com", "123 Main St", List.of());
        given(userService.createUser(request)).willReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getUserReturnsProfile() throws Exception {
        UserResponse response = new UserResponse(1L, "alice", "Alice Carter", "alice@example.com", "123 Main St", List.of());
        given(userService.getUserByUsername("alice")).willReturn(response);

        mockMvc.perform(get("/api/users/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void addCreditCardReturnsCreatedCard() throws Exception {
        CreateCreditCardRequest request = new CreateCreditCardRequest("4111111111111111", "Alice Carter", 12, 2030);
        CreditCardResponse response = new CreditCardResponse(9L, "Alice Carter", "****1111", 12, 2030);
        given(userService.addCreditCard("alice", request)).willReturn(response);

        mockMvc.perform(post("/api/users/alice/credit-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.maskedCardNumber").value("****1111"));
    }
}
