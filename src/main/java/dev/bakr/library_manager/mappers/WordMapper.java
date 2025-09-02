package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Word;
import dev.bakr.library_manager.requests.WordDtoRequest;
import dev.bakr.library_manager.responses.WordDtoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {
    Word toEntity(WordDtoRequest wordDtoRequest);

    WordDtoResponse toDto(Word word);
}
