package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.SubscriptionTokenEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.SubscriptionTokenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class SubscriptionTokenService {

    public static final String TYPE_VERIFICATION = "VERIFICATION";
    public static final String TYPE_MANAGEMENT = "MANAGEMENT";

    private static final int TOKEN_BYTES = 32;
    private static final int VERIFICATION_VALID_HOURS = 24;
    private static final int MANAGEMENT_VALID_MINUTES = 60;

    private final SubscriptionTokenJpaRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public SubscriptionTokenService(
            SubscriptionTokenJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public String createVerificationToken(AppUserEntity user) {
        return createToken(
                user,
                TYPE_VERIFICATION,
                LocalDateTime.now()
                        .plusHours(VERIFICATION_VALID_HOURS)
        );
    }

    @Transactional
    public String createManagementToken(AppUserEntity user) {
        return createToken(
                user,
                TYPE_MANAGEMENT,
                LocalDateTime.now()
                        .plusMinutes(MANAGEMENT_VALID_MINUTES)
        );
    }

    @Transactional(readOnly = true)
    public SubscriptionTokenEntity validateVerificationToken(
            String rawToken
    ) {
        return validateToken(
                rawToken,
                TYPE_VERIFICATION
        );
    }

    @Transactional(readOnly = true)
    public SubscriptionTokenEntity validateManagementToken(
            String rawToken
    ) {
        return validateToken(
                rawToken,
                TYPE_MANAGEMENT
        );
    }

    @Transactional
    public AppUserEntity consumeVerificationToken(
            String rawToken
    ) {
        SubscriptionTokenEntity token =
                validateToken(
                        rawToken,
                        TYPE_VERIFICATION
                );

        token.markUsed();

        return token.getUser();
    }

    private String createToken(
            AppUserEntity user,
            String tokenType,
            LocalDateTime expiresAt
    ) {
        byte[] randomBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        String tokenHash = hash(rawToken);

        repository.save(
                SubscriptionTokenEntity.create(
                        user,
                        tokenHash,
                        tokenType,
                        expiresAt
                )
        );

        return rawToken;
    }

    private SubscriptionTokenEntity validateToken(
            String rawToken,
            String tokenType
    ) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Der Link ist ungültig."
            );
        }

        String tokenHash = hash(rawToken);

        SubscriptionTokenEntity token =
                repository
                        .findByTokenHashAndTokenType(
                                tokenHash,
                                tokenType
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Der Link ist ungültig."
                                )
                        );

        if (token.isUsed()) {
            throw new IllegalArgumentException(
                    "Der Link wurde bereits verwendet."
            );
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException(
                    "Der Link ist abgelaufen."
            );
        }

        return token;
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashed =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hashed);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 ist nicht verfügbar.",
                    e
            );
        }
    }
}