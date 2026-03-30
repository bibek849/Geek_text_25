package com.bookstore.geek_text.author;

import com.bookstore.geek_text.author.dto.AuthorResponse;
import com.bookstore.geek_text.author.dto.CreateAuthorRequest;
import com.bookstore.geek_text.book.dto.BookResponse;
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

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void createAuthorReturnsCreatedAuthor() throws Exception {
        CreateAuthorRequest request = new CreateAuthorRequest("Robert", "Martin", "Software craftsman.", "Prentice Hall");
        given(authorService.createAuthor(request)).willReturn(new AuthorResponse(3L, "Robert", "Martin", "Software craftsman.", "Prentice Hall"));

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.lastName").value("Martin"));
    }

    @Test
    void getBooksByAuthorReturnsBooks() throws Exception {
        BookResponse response = new BookResponse(
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
        given(authorService.getBooksByAuthorId(3L)).willReturn(List.of(response));

        mockMvc.perform(get("/api/authors/3/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorId").value(3))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }
}
