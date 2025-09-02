package dev.bakr.library_manager.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryDtoResponse(Long id, String name) {
}
