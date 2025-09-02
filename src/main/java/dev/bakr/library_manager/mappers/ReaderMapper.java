package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Reader;
import dev.bakr.library_manager.requests.RegisterReaderDtoRequest;
import dev.bakr.library_manager.responses.RegisterReaderDtoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReaderMapper {
    Reader toEntity(RegisterReaderDtoRequest registerReaderDtoRequest);

    RegisterReaderDtoResponse toDto(Reader reader);
}
