package com.bookstore.geek_text.book;

import com.bookstore.geek_text.book.dto.BookResponse;
import com.bookstore.geek_text.book.dto.CreateBookRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    void getBooksFiltersByGenre() throws Exception {
        given(bookService.getBooks("Java", null)).willReturn(List.of(bookResponse(2L, "Effective Java")));

        mockMvc.perform(get("/api/books").param("genre", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Effective Java"))
                .andExpect(jsonPath("$[0].author").value("Joshua Bloch"));
    }

    @Test
    void getTopSellersReturnsSortedBooks() throws Exception {
        given(bookService.getTopSellers()).willReturn(List.of(bookResponse(1L, "Clean Code")));

        mockMvc.perform(get("/api/books/top-sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].copiesSold").value(9200))
                .andExpect(jsonPath("$[0].averageRating").value(4.67));
    }

    @Test
    void createBookReturnsCreatedBook() throws Exception {
        CreateBookRequest request = new CreateBookRequest(
                "9780321356680",
                "Effective Unit Testing",
                "Practical guidance for better tests.",
                11L,
                "Software Engineering",
                "Manning",
                2013,
                new BigDecimal("37.50"),
                1500
        );

        BookResponse response = new BookResponse(
                25L,
                request.isbn(),
                request.title(),
                request.description(),
                request.authorId(),
                "Lasse Koskela",
                request.genre(),
                request.publisher(),
                request.yearPublished(),
                request.price(),
                request.copiesSold(),
                new BigDecimal("0.00")
        );
        given(bookService.createBook(request)).willReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(25))
                .andExpect(jsonPath("$.isbn").value("9780321356680"))
                .andExpect(jsonPath("$.author").value("Lasse Koskela"));
    }

    @Test
    void discountBooksByPublisherReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/books/discount")
                        .param("publisher", "Addison-Wesley")
                        .param("percent", "10"))
                .andExpect(status().isNoContent());

        verify(bookService).discountBooksByPublisher(eq("Addison-Wesley"), eq(new BigDecimal("10")));
    }

    private BookResponse bookResponse(Long id, String title) {
        return new BookResponse(
                id,
                "9780134685991",
                title,
                "Detailed technical reference.",
                7L,
                "Joshua Bloch",
                "Java",
                "Addison-Wesley",
                2018,
                new BigDecimal("45.00"),
                9200,
                new BigDecimal("4.67")
        );
    }
}
