package com.milado_api_main.controller;

import com.milado_api_main.config.PublicEndpoint;
import com.milado_api_main.dto.ExceptionResponseDto;
import com.milado_api_main.dto.TokenSuccessResponseDto;
import com.milado_api_main.dto.UserLoginRequestDto;
import com.milado_api_main.exception.TokenVerificationException;
import com.milado_api_main.services.AuthenticationService;
import com.milado_api_main.utility.RefreshTokenHeaderProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
@Slf4j
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final RefreshTokenHeaderProvider refreshTokenHeaderProvider;

	@PublicEndpoint
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Logs in user into the system", description = "Returns Access-token and Refresh-token on successful authentication which provides access to protected endpoints")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Authentication successful"),
			@ApiResponse(responseCode = "401", description = "Bad credentials provided. Failed to authenticate user",
					content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))),
			@ApiResponse(responseCode = "422", description = "Password has been compromised",
					content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))) })
	public ResponseEntity<TokenSuccessResponseDto> login(@Valid @RequestBody final UserLoginRequestDto userLoginRequest) {

		final var tokenResponse = authenticationService.login(userLoginRequest);
		return ResponseEntity.ok(tokenResponse);
	}

	@PublicEndpoint
	@PutMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Refreshes Access-Token for a user", description = "Provides a new Access-token against the user for which the non expired refresh-token is provided")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Access-token refreshed"),
			@ApiResponse(responseCode = "403", description = "Refresh token has expired. Failed to refresh access token",
					content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))) })
	public ResponseEntity<TokenSuccessResponseDto> refreshToken() {
		final var refreshToken = refreshTokenHeaderProvider.getRefreshToken().orElseThrow(TokenVerificationException::new);
		final var tokenResponse = authenticationService.refreshToken(refreshToken);
		return ResponseEntity.ok(tokenResponse);
	}


}