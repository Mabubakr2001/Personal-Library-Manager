package dev.bakr.library_manager.responses;

public record LoginReaderDtoResponse(String jwtToken, Long jwtExpirationMs, String jwtExpirationText) {
}
