package com.milado_api_main.controller;

import com.milado_api_main.config.PublicEndpoint;
import com.milado_api_main.dto.AccountDto;
import com.milado_api_main.dto.ExceptionResponseDto;
import com.milado_api_main.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/accounts")
@Tag(name = "Account Management", description = "Endpoints for managing account profile details")
public class AccountController {

    private final AccountService accountService;
    @PublicEndpoint
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates a user account", description = "Registers a unique user record in the system corresponding to the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "409", description = "User account with provided email-id already exists",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))),
            @ApiResponse(responseCode = "422", description = "Provided password is compromised",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))) })
    public ResponseEntity<AccountDto> createUser(@Valid @RequestBody final AccountDto accountCreationRequest) {
        AccountDto accountDto = accountService.create(accountCreationRequest);
        return new ResponseEntity<>(accountDto, HttpStatus.CREATED);
    }
}
