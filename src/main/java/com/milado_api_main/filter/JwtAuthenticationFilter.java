package com.milado_api_main.filter;

import com.milado_api_main.utility.ApiEndpointSecurityInspector;
import com.milado_api_main.utility.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtils;

    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        final var unsecuredApiBeingInvoked = apiEndpointSecurityInspector.isUnsecureRequest(request);
        if (Boolean.FALSE.equals(unsecuredApiBeingInvoked)) {
            final var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (StringUtils.isNotEmpty(authorizationHeader)) {
                if (authorizationHeader.startsWith(BEARER_PREFIX)) {
                    final var token = authorizationHeader.replace(BEARER_PREFIX, StringUtils.EMPTY);
                    final var authorities = jwtUtils.getAuthority(token);
                    final var userId = jwtUtils.extractUserId(token);
                    final var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
