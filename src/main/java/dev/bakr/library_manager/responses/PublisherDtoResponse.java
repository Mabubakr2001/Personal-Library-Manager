package dev.bakr.library_manager.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PublisherDtoResponse(Long id, String name) {
}
