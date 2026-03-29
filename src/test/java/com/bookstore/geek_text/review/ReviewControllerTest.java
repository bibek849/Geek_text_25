package com.bookstore.geek_text.review;

import com.bookstore.geek_text.review.dto.AverageRatingResponse;
import com.bookstore.geek_text.review.dto.CommentResponse;
import com.bookstore.geek_text.review.dto.CreateCommentRequest;
import com.bookstore.geek_text.review.dto.CreateRatingRequest;
import com.bookstore.geek_text.review.dto.RatingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void createRatingReturnsCreatedRating() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest(1L, 5);
        RatingResponse response = new RatingResponse(7L, 3L, 1L, "alice", 5, LocalDateTime.of(2026, 3, 24, 12, 0));
        given(reviewService.createRating(3L, request)).willReturn(response);

        mockMvc.perform(post("/api/books/3/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookId").value(3))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getAverageRatingReturnsDecimalValue() throws Exception {
        given(reviewService.getAverageRating(3L)).willReturn(new AverageRatingResponse(3L, new BigDecimal("4.50")));

        mockMvc.perform(get("/api/books/3/ratings/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void getCommentsReturnsList() throws Exception {
        CommentResponse response = new CommentResponse(11L, 3L, 1L, "alice", "Great book", LocalDateTime.of(2026, 3, 24, 12, 0));
        given(reviewService.getCommentsByBookId(3L)).willReturn(List.of(response));

        mockMvc.perform(get("/api/books/3/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Great book"));
    }

    @Test
    void createCommentReturnsCreatedComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest(1L, "Great book");
        CommentResponse response = new CommentResponse(11L, 3L, 1L, "alice", "Great book", LocalDateTime.of(2026, 3, 24, 12, 0));
        given(reviewService.createComment(3L, request)).willReturn(response);

        mockMvc.perform(post("/api/books/3/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.username").value("alice"));
    }
}
