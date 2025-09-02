package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.responses.ReaderDtoResponse;
import dev.bakr.library_manager.service.ReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
public class ReaderController {
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping("/readers/me")
    public ResponseEntity<ReaderDtoResponse> getAuthenticatedReader() {
        ReaderDtoResponse authenticatedReader = readerService.getAuthenticatedReader();
        return ResponseEntity.ok(authenticatedReader);
    }
}
