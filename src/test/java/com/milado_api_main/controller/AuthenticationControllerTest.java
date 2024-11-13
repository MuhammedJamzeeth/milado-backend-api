package com.milado_api_main.controller;

import com.milado_api_main.config.CustomAuthenticationEntryPoint;
import com.milado_api_main.config.SecurityConfiguration;
import com.milado_api_main.dto.TokenSuccessResponseDto;
import com.milado_api_main.dto.UserLoginRequestDto;
import com.milado_api_main.exception.ExceptionResponseHandler;
import com.milado_api_main.exception.InvalidCredentialsException;
import com.milado_api_main.services.AuthenticationService;
import com.milado_api_main.utility.ApiEndpointSecurityInspector;
import com.milado_api_main.utility.JwtUtility;
import com.milado_api_main.utility.RefreshTokenHeaderProvider;
import io.swagger.v3.core.util.Json;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@Import({ ExceptionResponseHandler.class, SecurityConfiguration.class, CustomAuthenticationEntryPoint.class, ApiEndpointSecurityInspector.class })
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @SpyBean
    private RefreshTokenHeaderProvider refreshTokenHeaderProvider;

    @MockBean
    private JwtUtility jwtUtility;

    @Test
    @SneakyThrows
    void shouldReturnUnauthorizedAgainstInvalidLoginCredentials() {
        // prepare user login request
        final var userLoginRequest = new UserLoginRequestDto();
        userLoginRequest.setEmailId("mail@domain.ut");
        userLoginRequest.setPassword("test-password");
        userLoginRequest.setIdToken("test-id-token");

        // mock service layer to throw InvalidLoginCredentialsException
        when(authenticationService.login(refEq(userLoginRequest))).thenThrow(new InvalidCredentialsException("Invalid login credentials provided."));

        // execute API request
        final var apiPath = "/auth/login";
        final var requestBody = Json.mapper().writeValueAsString(userLoginRequest);
        mockMvc.perform(post(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andExpect(jsonPath("$.Status").value(HttpStatus.UNAUTHORIZED.toString()))
                .andExpect(jsonPath("$.Description").value("Invalid login credentials provided."));

        // verify mock interaction
        verify(authenticationService).login(refEq(userLoginRequest));
    }
    @Test
    @SneakyThrows
    void shouldReturnAccessTokenForValidLoginRequest() {
        // prepare user login request
        final var userLoginRequest = new UserLoginRequestDto();
        userLoginRequest.setEmailId("mail@domain.ut");
        userLoginRequest.setPassword("test-password");
        userLoginRequest.setIdToken("test-id-token");

        // prepare service layer success response
        final var accessToken = "test-access-token";
        final var refreshToken = "test-refresh-token";
        final var tokenResponse = mock(TokenSuccessResponseDto.class);
        when(tokenResponse.getAccessToken()).thenReturn(accessToken);
        when(tokenResponse.getRefreshToken()).thenReturn(refreshToken);
        when(authenticationService.login(refEq(userLoginRequest))).thenReturn(tokenResponse);

        // execute API request
        final var apiPath = "/auth/login";
        final var requestBody = Json.mapper().writeValueAsString(userLoginRequest);
        mockMvc.perform(post(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.AccessToken").value(accessToken))
                .andExpect(jsonPath("$.RefreshToken").value(refreshToken));

        // verify mock interaction
        verify(authenticationService).login(refEq(userLoginRequest));
    }
}
