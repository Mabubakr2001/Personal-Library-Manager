package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Book;
import dev.bakr.library_manager.requests.BookDtoRequest;
import dev.bakr.library_manager.responses.BookDtoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDtoResponse toDto(Book book);

    Book toEntity(BookDtoRequest bookDtoRequest);
}
