package dev.bakr.library_manager.responses;

public record ReaderDtoResponse(Long id,
        String username,
        String email,
        Boolean isEnabled) {
}
