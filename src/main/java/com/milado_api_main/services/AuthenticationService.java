package com.milado_api_main.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.milado_api_main.config.TokenConfigurationProperties;
import com.milado_api_main.dto.TokenSuccessResponseDto;
import com.milado_api_main.dto.UserLoginRequestDto;
import com.milado_api_main.exception.InvalidCredentialsException;
import com.milado_api_main.exception.TokenVerificationException;
import com.milado_api_main.repositries.AccountRepository;
import com.milado_api_main.utility.AuthenticatedUserIdProvider;
import com.milado_api_main.utility.CacheManager;
import com.milado_api_main.utility.JwtUtility;
import com.milado_api_main.utility.RefreshTokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenConfigurationProperties.class)
@Slf4j
public class AuthenticationService {

    private final JwtUtility jwtUtility;
    private final CacheManager cacheManager;
    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final CompromisedPasswordChecker compromisedPasswordChecker;
    private final TokenConfigurationProperties tokenConfigurationProperties;
    private final AuthenticatedUserIdProvider authenticatedUserIdProvider;

    public TokenSuccessResponseDto login(@NonNull final UserLoginRequestDto userLoginRequestDto) {
        final var user = userRepository.findByEmail(userLoginRequestDto.getEmailId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login credentials provided."));

        final var encodedPassword = user.getPassword();
        final var plainTextPassword = userLoginRequestDto.getPassword();
        final var idToken = userLoginRequestDto.getIdToken();
        final var isCorrectPassword = passwordEncoder.matches(plainTextPassword, encodedPassword);

        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException("Invalid login credentials provided.");
        }

//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
//            String uid = decodedToken.getUid();
////            log.info("Firebase UID: " + uid);
//        } catch (FirebaseAuthException e) {
//            throw new InvalidCredentialsException("Invalid token credentials provided.");
//        }

        final var isPasswordCompromised = compromisedPasswordChecker.check(plainTextPassword).isCompromised();
        if (Boolean.TRUE.equals(isPasswordCompromised)) {
            throw new CompromisedPasswordException("Password has been compromised. Password reset required.");
        }

        final var accessToken = jwtUtility.generateAccessToken(user);
        final var refreshToken = refreshTokenGenerator.generate();
        final var refreshTokenValidity = tokenConfigurationProperties.getRefreshToken().getValidity();
        cacheManager.save(refreshToken, user.getId(), Duration.ofMinutes(refreshTokenValidity));

        return TokenSuccessResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenSuccessResponseDto refreshToken(@NonNull final String refreshToken) {
        final var userId = cacheManager.fetch(refreshToken, Long.class).orElseThrow(TokenVerificationException::new);
        final var user = userRepository.getReferenceById(userId);
        final var accessToken = jwtUtility.generateAccessToken(user);

        return TokenSuccessResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }

}