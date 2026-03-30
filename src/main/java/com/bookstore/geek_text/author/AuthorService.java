package com.bookstore.geek_text.author;

import com.bookstore.geek_text.author.dto.AuthorResponse;
import com.bookstore.geek_text.author.dto.CreateAuthorRequest;
import com.bookstore.geek_text.book.BookService;
import com.bookstore.geek_text.book.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookService bookService;

    public AuthorService(AuthorRepository authorRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookService = bookService;
    }

    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        Author author = new Author(
                request.firstName(),
                request.lastName(),
                request.biography(),
                request.publisher()
        );
        return AuthorResponse.from(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthorId(Long authorId) {
        authorRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Author not found for id: " + authorId));
        return bookService.getBooksByAuthorId(authorId);
    }
}
