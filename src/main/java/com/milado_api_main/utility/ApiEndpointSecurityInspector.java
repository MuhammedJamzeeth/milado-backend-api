package com.milado_api_main.utility;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.milado_api_main.config.OpenApiConfigurationProperties;
import com.milado_api_main.config.PublicEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;



import io.swagger.v3.oas.models.PathItem.HttpMethod;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
@Slf4j
public class ApiEndpointSecurityInspector {

	private final RequestMappingHandlerMapping requestHandlerMapping;
	private final OpenApiConfigurationProperties openApiConfigurationProperties;
	
	private static final List<String> SWAGGER_V3_PATHS = List.of("/swagger-ui**/**", "/v3/api-docs**/**");
	
	@Getter
	private List<String> publicGetEndpoints = new ArrayList<String>();
	@Getter
	private List<String> publicPostEndpoints = new ArrayList<String>();
	@Getter
	private List<String> publicPutEndpoints = new ArrayList<String>();

	@PostConstruct
	public void init() {
		final var handlerMethods = requestHandlerMapping.getHandlerMethods();
		handlerMethods.forEach((requestInfo, handlerMethod) -> {
			if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
				final var httpMethod = requestInfo.getMethodsCondition().getMethods().iterator().next().asHttpMethod();
				final var apiPaths = requestInfo.getPathPatternsCondition().getPatternValues();
				if (httpMethod.equals(GET)) {
					publicGetEndpoints.addAll(apiPaths);
				} else if (httpMethod.equals(POST)) {
					publicPostEndpoints.addAll(apiPaths);
				} else if (httpMethod.equals(PUT)) {
					publicPutEndpoints.addAll(apiPaths);
				}
			}
		});
		
		final var openApiEnabled = openApiConfigurationProperties.getOpenApi().isEnabled();
		if (Boolean.TRUE.equals(openApiEnabled)) {
			publicGetEndpoints.addAll(SWAGGER_V3_PATHS);
		}
	}

	public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
		final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
		var unsecuredApiPaths = getUnsecuredApiPaths(requestHttpMethod);
		unsecuredApiPaths = Optional.ofNullable(unsecuredApiPaths).orElseGet(ArrayList::new);

		return unsecuredApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
	}

	private List<String> getUnsecuredApiPaths(@NonNull final HttpMethod httpMethod) {
		switch (httpMethod) {
			case GET:
				return publicGetEndpoints;
			case POST:
				return publicPostEndpoints;
			case PUT:
				return publicPutEndpoints;
			default:
				return Collections.emptyList();
		}
	}
	
}