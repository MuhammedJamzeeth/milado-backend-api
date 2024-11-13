package com.milado_api_main.utility;

import com.milado_api_main.config.TokenConfigurationProperties;
import com.milado_api_main.entities.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@EnableConfigurationProperties(TokenConfigurationProperties.class)
@Slf4j
public class JwtUtility {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SCOPE_CLAIM_NAME = "scp";
    private final TokenConfigurationProperties tokenConfigurationProperties;

    public JwtUtility(TokenConfigurationProperties tokenConfigurationProperties) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
    }

    public String generateAccessToken(@NonNull final Account user) {
        final var jti = String.valueOf(UUID.randomUUID());
        final var audience = String.valueOf(user.getId());
        final var accessTokenValidity = tokenConfigurationProperties.getAccessToken().getValidity();
        final var expiration = TimeUnit.MINUTES.toMillis(accessTokenValidity);
        final var currentTimestamp = new Date(System.currentTimeMillis());
        final var expirationTimestamp = new Date(System.currentTimeMillis() + expiration);
        final var scopes = String.join(StringUtils.SPACE, user.getUserStatus().getScopes());

        final var claims = new HashMap<String, String>();
        claims.put(SCOPE_CLAIM_NAME, scopes);

        return Jwts.builder()
                .claims(claims)
                .id(jti)
                .issuedAt(currentTimestamp)
                .expiration(expirationTimestamp)
                .audience().add(audience)
                .and()
                .signWith(getPrivateKey(), Jwts.SIG.RS512)
                .compact();
    }


    private <T> T extractClaim(@NonNull final String token, @NonNull final Function<Claims, T> claimsResolver) {
        final var sanitizedToken = token.replace(BEARER_PREFIX, StringUtils.EMPTY);
        final var claims = Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(sanitizedToken)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public Duration getTimeUntilExpiration(@NonNull final String token) {
        final var expirationTimestamp = extractClaim(token, Claims::getExpiration).toInstant();
        final var currentTimestamp = new Date().toInstant();
        return Duration.between(currentTimestamp, expirationTimestamp);
    }
    public List<GrantedAuthority> getAuthority(@NonNull final String token){
        final var scopes = extractClaim(token, claims -> claims.get(SCOPE_CLAIM_NAME, String.class));
        return Arrays.stream(scopes.split(StringUtils.SPACE))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    public String getJti(@NonNull final String token) {
        return extractClaim(token, Claims::getId);
    }

    @SneakyThrows
    private PrivateKey getPrivateKey() {
        final var privateKey = tokenConfigurationProperties.getAccessToken().getPrivateKey();
        final var sanitizedPrivateKey = sanitizeKey(privateKey);

        final var decodedPrivateKey = Decoders.BASE64.decode(sanitizedPrivateKey);
        final var spec = new PKCS8EncodedKeySpec(decodedPrivateKey);

        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }
    public Long extractUserId(@NonNull final String token) {
        final var audience = extractClaim(token, Claims::getAudience).iterator().next();
        return Long.parseLong(audience);
    }

    @SneakyThrows
    private PublicKey getPublicKey() {
        final var publicKey = tokenConfigurationProperties.getAccessToken().getPublicKey();
        final var sanitizedPublicKey = sanitizeKey(publicKey);

        final var decodedPublicKey = Decoders.BASE64.decode(sanitizedPublicKey);
        final var spec = new X509EncodedKeySpec(decodedPublicKey);

        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private String sanitizeKey(@NonNull final String key) {
        return key
                .replace("-----BEGIN PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----END PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----BEGIN PRIVATE KEY-----", StringUtils.EMPTY)
                .replace("-----END PRIVATE KEY-----", StringUtils.EMPTY)
                .replaceAll("\\n", StringUtils.EMPTY)
                .replaceAll("\\s", StringUtils.EMPTY);
    }

}
