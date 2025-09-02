package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requests.QuoteDtoRequest;
import dev.bakr.library_manager.responses.QuoteDtoResponse;
import dev.bakr.library_manager.service.QuoteService;
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
@RequestMapping(path = "/api/v1/readers/me/books")
@Tag(name = "Quote")
// For OpenAPI to update the Swagger UI to show all the returned HTTP status codes
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "403", description = "Don't have access to the resources"),
        @ApiResponse(responseCode = "401", description = "Unauthorized due to invalid inputs or credentials")
})
public class QuoteController {
    private final QuoteService quoteService;

    QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Operation(summary = "Adds a new quote to the quotes collection of the logged in reader")
    @PostMapping("/{bookId}/quotes")
    public ResponseEntity<QuoteDtoResponse> addQuote(@PathVariable Long bookId,
            @Valid @RequestBody QuoteDtoRequest quoteDtoRequest) {
        QuoteDtoResponse addedQuote = quoteService.addQuote(bookId, quoteDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedQuote);
    }

    @Operation(summary = "Finds a certain quote by its id and updates it accordingly")
    @PutMapping("/{bookId}/quotes/{quoteId}")
    public ResponseEntity<QuoteDtoResponse> updateQuote(@PathVariable Long bookId, @PathVariable Long quoteId,
            @Valid @RequestBody QuoteDtoRequest quoteDtoRequest) {
        QuoteDtoResponse updatedQuote = quoteService.updateQuote(bookId, quoteId, quoteDtoRequest);
        return ResponseEntity.ok(updatedQuote);
    }

    @Operation(summary = "Finds a certain quote by its id and deletes from the list")
    @DeleteMapping("/{bookId}/quotes/{quoteId}")
    public ResponseEntity<String> deleteQuote(@PathVariable Long bookId, @PathVariable Long quoteId) {
        String deletedQuoteMessage = quoteService.deleteQuote(bookId, quoteId);
        return ResponseEntity.ok(deletedQuoteMessage);
    }


    @Operation(summary = "Gets a certain quote by its id from the list of quotes")
    @GetMapping("/{bookId}/quotes/{quoteId}")
    public ResponseEntity<QuoteDtoResponse> getQuote(@PathVariable Long bookId, @PathVariable Long quoteId) {
        QuoteDtoResponse returnedQuote = quoteService.getQuote(bookId, quoteId);
        return ResponseEntity.ok(returnedQuote);
    }

    @Operation(summary = "Return all the quotes list for a specified reader")
    @GetMapping("/{bookId}/quotes")
    public ResponseEntity<List<QuoteDtoResponse>> getQuotes(@PathVariable Long bookId) {
        List<QuoteDtoResponse> returnedQuotes = quoteService.getQuotes(bookId);
        return ResponseEntity.ok(returnedQuotes);
    }
}
