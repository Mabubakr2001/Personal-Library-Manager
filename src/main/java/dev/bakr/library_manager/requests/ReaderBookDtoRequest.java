package dev.bakr.library_manager.requests;

import jakarta.validation.constraints.NotBlank;

public record ReaderBookDtoRequest(@NotBlank(message = "The book should have a status!") String status,
        Integer leftOffPage) {
}
