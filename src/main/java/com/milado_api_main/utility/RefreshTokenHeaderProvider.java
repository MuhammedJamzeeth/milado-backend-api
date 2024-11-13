package com.milado_api_main.utility;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class RefreshTokenHeaderProvider {
	
	private final HttpServletRequest httpServletRequest;
	
	public static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

	public Optional<String> getRefreshToken() {
		return Optional.ofNullable(httpServletRequest.getHeader(REFRESH_TOKEN_HEADER))
				.filter(StringUtils::isNotBlank);
	}

}
