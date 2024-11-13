package com.milado_api_main.utility;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class AuthenticatedUserIdProvider {
	public Long getUserId() {
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
		        .map(Authentication::getPrincipal)
		        .filter(Long.class::isInstance)
		        .map(Long.class::cast)
		        .orElseThrow(IllegalStateException::new);
	}

}
