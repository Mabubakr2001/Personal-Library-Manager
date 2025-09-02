package dev.bakr.library_manager.responses;

public record WordDtoResponse(Long id,
        String wordContent,
        String translation,
        String relatedSentence,
        Integer pageNumber) {
}
