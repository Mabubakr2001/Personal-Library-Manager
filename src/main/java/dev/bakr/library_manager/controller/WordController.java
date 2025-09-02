package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requests.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDtoResponse;
import dev.bakr.library_manager.service.WordService;
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
@Tag(name = "Word")
// For OpenAPI to update the Swagger UI to show all the returned HTTP status codes
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "403", description = "Don't have access to the resources"),
        @ApiResponse(responseCode = "401", description = "Unauthorized due to invalid inputs or credentials")
})
public class WordController {
    private final WordService wordService;

    WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @Operation(summary = "Add a new word to the words collection of the reader")
    @PostMapping("/{bookId}/words")
    public ResponseEntity<WordDtoResponse> addWord(@PathVariable Long bookId, @Valid @RequestBody WordDtoRequest wordDtoRequest) {
        WordDtoResponse addedWord = wordService.addWord(bookId, wordDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedWord);
    }

    @Operation(summary = "Update a certain word by it's id")
    @PutMapping("/{bookId}/words/{wordId}")
    public ResponseEntity<WordDtoResponse> updateWord(@PathVariable Long bookId, @PathVariable Long wordId, @Valid @RequestBody WordDtoRequest wordDtoRequest) {
        WordDtoResponse updatedWord = wordService.updateWord(bookId, wordId, wordDtoRequest);
        return ResponseEntity.ok(updatedWord);
    }

    @Operation(summary = "Delete the chosen word from the words collection")
    @DeleteMapping("/{bookId}/words/{wordId}")
    public ResponseEntity<String> deleteWord(@PathVariable Long bookId, @PathVariable Long wordId) {
        String deletedWordMessage = wordService.deleteWord(bookId, wordId);
        return ResponseEntity.ok(deletedWordMessage);
    }


    @Operation(summary = "Extract a certain word by it's it from the words list")
    @GetMapping("/{bookId}/words/{wordId}")
    public ResponseEntity<WordDtoResponse> getWord(@PathVariable Long bookId, @PathVariable Long wordId) {
        WordDtoResponse returnedWord = wordService.getWord(bookId, wordId);
        return ResponseEntity.ok(returnedWord);
    }

    @Operation(summary = "Get the books list for a specified reader")
    @GetMapping("/{bookId}/words")
    public ResponseEntity<List<WordDtoResponse>> getWords(@PathVariable Long bookId) {
        List<WordDtoResponse> returnedWords = wordService.getWords(bookId);
        return ResponseEntity.ok(returnedWords);
    }
}
