package com.milado_api_main.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.milado")
public class OpenApiConfigurationProperties {

	private OpenAPI openApi = new OpenAPI();

	@Getter
	@Setter
	public static class OpenAPI {

		private boolean enabled;

		private String title;
		private String description;
		private String apiVersion;
		
	}

}