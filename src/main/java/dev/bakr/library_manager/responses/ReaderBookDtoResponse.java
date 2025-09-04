package dev.bakr.library_manager.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.bakr.library_manager.model.Quote;
import dev.bakr.library_manager.model.Word;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ReaderBookDtoResponse(Long id,
        String title,
        String subtitle,
        String description,
        String isbn,
        Integer pagesCount,
        String imageLink,
        String printingType,
        Integer publishingYear,
        String authorName,
        String categoryName,
        String publisherName,
        String readingStatus,
        LocalDate addingDate,
        Integer leftOffPage,
        List<Quote> quotes,
        List<Word> words) {
}
