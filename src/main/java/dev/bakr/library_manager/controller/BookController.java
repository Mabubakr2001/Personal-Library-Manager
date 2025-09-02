package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requests.BookDtoRequest;
import dev.bakr.library_manager.requests.ReaderBookDtoRequest;
import dev.bakr.library_manager.responses.ReaderBookDtoResponse;
import dev.bakr.library_manager.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/readers/me")
// For OpenAPI (instead of book-controller on Swagger UI)
@Tag(name = "Book")
// For OpenAPI to update the Swagger UI to show all the returned HTTP status codes
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK! Found"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "403", description = "Don't have access to the resources"),
        @ApiResponse(responseCode = "401", description = "Unauthorized due to invalid inputs or credentials")
})
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // For OpenAPI to show more info about a certain request.
    @Operation(summary = "Gets all books for the logged in reader", description = "Returns the collection of book that the reader has")
    @GetMapping(path = "/books")
    public ResponseEntity<List<ReaderBookDtoResponse>> getReaderBooks() {
        List<ReaderBookDtoResponse> allBooks = bookService.getReaderBooks();
        return ResponseEntity.ok(allBooks);
    }

    @Operation(summary = "Gets a certain book by its ID for the logged in reader", description = "Returns the book that has the entered ID")
    @GetMapping(path = "/books/{bookId}")
    public ResponseEntity<ReaderBookDtoResponse> getReaderBook(@PathVariable Long bookId) {
        ReaderBookDtoResponse readingCopy = bookService.getReaderBook(bookId);  // this might throw BookNotFoundException
        return ResponseEntity.status(HttpStatus.OK).body(readingCopy);
    }

    @Operation(summary = "Adds a book to the collection for the logged in reader with the entered ID", description = "Returns the new book that was added to the collection with all its info")
    @PostMapping(path = "/books")
    public ResponseEntity<String> addReaderBook(@Valid @RequestBody BookDtoRequest bookDtoRequest) {
        String newBookMessage = bookService.addReaderBook(bookDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBookMessage);
    }

    @Operation(summary = "Updates a certain book by its ID for the logged in reader", description = "Returns the book that was updated with all its info")
    @PutMapping(path = "/books/{bookId}")
    public ResponseEntity<ReaderBookDtoResponse> updateReaderBook(@PathVariable Long bookId,
            @Valid @RequestBody ReaderBookDtoRequest readerBookDtoRequest) {
        ReaderBookDtoResponse updatedReadingCopy = bookService.updateReaderBook(bookId, readerBookDtoRequest);
        return ResponseEntity.ok(updatedReadingCopy);
    }

    @Operation(summary = "Deletes a certain book by its ID for the logged in reader", description = "Returns a message saying that this book has been successfully deleted")
    @DeleteMapping(path = "/books/{bookId}")
    public ResponseEntity<String> deleteReaderBook(@PathVariable Long bookId) {
        String deletedBookMessage = bookService.deleteReaderBook(bookId);
        return ResponseEntity.ok(deletedBookMessage);
    }
}
