package dev.bakr.library_manager.mappers;

import dev.bakr.library_manager.model.Quote;
import dev.bakr.library_manager.requests.QuoteDtoRequest;
import dev.bakr.library_manager.responses.QuoteDtoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    Quote toEntity(QuoteDtoRequest quoteDtoRequest);

    QuoteDtoResponse toDto(Quote quote);
}
