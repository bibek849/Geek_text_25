package com.bookstore.geek_text.author;

import com.bookstore.geek_text.author.dto.AuthorResponse;
import com.bookstore.geek_text.author.dto.CreateAuthorRequest;
import com.bookstore.geek_text.book.dto.BookResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping({"/api/authors", "/authors"})
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        return authorService.createAuthor(request);
    }

    @GetMapping("/{authorId}/books")
    public List<BookResponse> getBooksByAuthor(@PathVariable @Positive Long authorId) {
        return authorService.getBooksByAuthorId(authorId);
    }
}
