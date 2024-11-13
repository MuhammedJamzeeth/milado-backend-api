package com.milado_api_main.config;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties controlling token generation, validation, and
 * expiration within the application.
 */

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "com.milado.token")
public class TokenConfigurationProperties {

    @Valid
    private AccessToken accessToken = new AccessToken();

    @Valid
    private RefreshToken refreshToken = new RefreshToken();

    @Getter
    @Setter
    public static class AccessToken {

        @NotBlank
        private String privateKey;

        @NotBlank
        private String publicKey;

        @NotNull
        @Positive
        private Integer validity;

    }

    @Getter
    @Setter
    public class RefreshToken {

        @NotNull
        @Positive
        private Integer validity;

    }
}
