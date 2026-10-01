package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.SubscriptionTokenJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminUserService {

    private final AppUserJpaRepository userRepository;
    private final UserPreferenceJpaRepository preferenceRepository;
    private final SubscriptionTokenJpaRepository tokenRepository;

    public AdminUserService(
            AppUserJpaRepository userRepository,
            UserPreferenceJpaRepository preferenceRepository,
            SubscriptionTokenJpaRepository tokenRepository
    ) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getUser(
            UUID userId
    ) {
        return toResponse(
                findUser(userId)
        );
    }

    @Transactional
    public AdminUserResponse updateUser(
            UUID userId,
            AdminUserUpdateRequest request
    ) {
        AppUserEntity user =
                findUser(userId);

        if (request.email() != null) {
            String normalizedEmail =
                    request.email()
                            .trim()
                            .toLowerCase();

            userRepository
                    .findByEmail(normalizedEmail)
                    .filter(existing ->
                            !existing.getId().equals(userId)
                    )
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Diese E-Mail-Adresse wird bereits verwendet."
                        );
                    });

            user.updateEmail(normalizedEmail);
        }

        if (request.displayName() != null) {
            user.updateDisplayName(
                    request.displayName()
            );
        }

        boolean active =
                request.active() != null
                        ? request.active()
                        : user.isActive();

        boolean verified =
                request.verified() != null
                        ? request.verified()
                        : user.isVerified();

        user.updateStatus(
                active,
                verified
        );

        return toResponse(user);
    }

    @Transactional
    public AdminDeleteResponse deleteUser(
            UUID userId
    ) {
        AppUserEntity user =
                findUser(userId);

        String email =
                user.getEmail();

        /*
         * Foreign-key-safe deletion order:
         *
         * subscription_tokens -> app_users
         * user_preferences    -> app_users
         * app_users
         */

        tokenRepository.deleteByUserId(userId);

        List<UserPreferenceEntity> preferences =
                preferenceRepository.findByUserId(userId);

        if (!preferences.isEmpty()) {
            preferenceRepository.deleteAll(preferences);
        }

        userRepository.delete(user);

        return new AdminDeleteResponse(
                userId,
                email,
                "Benutzer wurde vollständig gelöscht."
        );
    }

    private AppUserEntity findUser(
            UUID userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Benutzer wurde nicht gefunden."
                        )
                );
    }

    private AdminUserResponse toResponse(
            AppUserEntity user
    ) {
        List<String> topics =
                preferenceRepository
                        .findByUserId(user.getId())
                        .stream()
                        .filter(UserPreferenceEntity::isActive)
                        .map(UserPreferenceEntity::getTopic)
                        .map(this::topicDisplayName)
                        .distinct()
                        .sorted()
                        .toList();

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.isActive(),
                user.isVerified(),
                topics,
                user.getCreatedAt()
        );
    }

    private String topicDisplayName(
            String topic
    ) {
        try {
            return Topic
                    .valueOf(topic)
                    .getDisplayName();
        } catch (IllegalArgumentException exception) {
            return topic;
        }
    }

    public record AdminUserResponse(
            UUID id,
            String email,
            String displayName,
            boolean active,
            boolean verified,
            List<String> topics,
            LocalDateTime createdAt
    ) {
    }

    public record AdminUserUpdateRequest(
            String email,
            String displayName,
            Boolean active,
            Boolean verified
    ) {
    }

    public record AdminDeleteResponse(
            UUID id,
            String email,
            String message
    ) {
    }
}