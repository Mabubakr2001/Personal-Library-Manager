package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requests.LoginReaderDtoRequest;
import dev.bakr.library_manager.requests.RegisterReaderDtoRequest;
import dev.bakr.library_manager.requests.VerifyReaderDtoRequest;
import dev.bakr.library_manager.responses.LoginReaderDtoResponse;
import dev.bakr.library_manager.responses.RegisterReaderDtoResponse;
import dev.bakr.library_manager.service.AuthReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Reader Authentication")
// For OpenAPI to update the Swagger UI to show all the returned HTTP status codes
@ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Found"),
//        @ApiResponse(responseCode = "404", description = "Not found"),
//        @ApiResponse(responseCode = "403", description = "Don't have access to the resources"),
        @ApiResponse(responseCode = "401", description = "Unauthorized due to invalid inputs or credentials")
})
public class AuthController {
    private final AuthReaderService authReaderService;

    public AuthController(AuthReaderService authReaderService) {
        this.authReaderService = authReaderService;
    }

    @Operation(summary = "Signs up a new using with his username, email, and password to add him to the database")
    @PostMapping("/signup")
    public ResponseEntity<RegisterReaderDtoResponse> registerReader(@Valid @RequestBody RegisterReaderDtoRequest registerReaderDtoRequest) {
        RegisterReaderDtoResponse registeredReader = authReaderService.registerReader(registerReaderDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredReader);
    }

    @Operation(summary = "Verifies the new reader that was added to the database by a 6-digit verification code")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyReader(@Valid @RequestBody VerifyReaderDtoRequest verifyReaderDtoRequest) {
        String verificationMessage = authReaderService.verifyReader(verifyReaderDtoRequest);
        return ResponseEntity.ok(verificationMessage);
    }

    @Operation(summary = "Logs in the new reader that has been verified so that he can access the resources")
    @PostMapping("/login")
    public ResponseEntity<LoginReaderDtoResponse> loginReader(@Valid @RequestBody LoginReaderDtoRequest loginReaderDtoRequest) {
        LoginReaderDtoResponse loginReaderDtoResponse = authReaderService.loginReader(loginReaderDtoRequest);
        return ResponseEntity.ok(loginReaderDtoResponse);
    }

    @Operation(summary = "Logs the reader out from the application, and that will block his recent JWT")
    @PostMapping("/logout")
    public ResponseEntity<String> logoutReader(HttpServletRequest request) {
        String loggedOutMessage = authReaderService.logoutReader(request);
        return ResponseEntity.ok(loggedOutMessage);
    }

    @Operation(summary = "Resends the verification code to the reader by email in case if he passed the expiration time of the verification code")
    @PostMapping("/resend")
    public ResponseEntity<String> reSendOTAC(@RequestParam String email) {
        String resendingMessage = authReaderService.reSendOTAC(email);
        return ResponseEntity.ok(resendingMessage);
    }
}
