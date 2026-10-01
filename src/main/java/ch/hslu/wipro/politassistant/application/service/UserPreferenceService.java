package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.AppUserJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import ch.hslu.wipro.politassistant.application.port.out.NotificationSender;

import ch.hslu.wipro.politassistant.domain.classification.Topic;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserPreferenceService {

    private final AppUserJpaRepository userRepository;
    private final UserPreferenceJpaRepository preferenceRepository;
    private final SubscriptionTokenService tokenService;
    private final NotificationSender notificationSender;
    private final String baseUrl;

    public UserPreferenceService(
            AppUserJpaRepository userRepository,
            UserPreferenceJpaRepository preferenceRepository,
            SubscriptionTokenService tokenService,
            NotificationSender notificationSender,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.tokenService = tokenService;
        this.notificationSender = notificationSender;
        this.baseUrl = normalizeBaseUrl(baseUrl);
    }

    @Transactional
    public ActionResponse requestSubscription(
            String email,
            String displayName,
            List<String> topics
    ) {
        String normalizedEmail = normalizeEmail(email);
        List<Topic> parsedTopics = parseTopics(topics);

        AppUserEntity user = userRepository
                .findByEmail(normalizedEmail)
                .orElseGet(() ->
                        userRepository.save(
                                AppUserEntity.createPending(
                                        normalizedEmail,
                                        displayName
                                )
                        )
                );

        if (displayName != null && !displayName.isBlank()) {
            user.updateDisplayName(displayName);
        }

        /*
         * An already active and verified user must not be allowed to
         * overwrite the subscription simply by knowing the e-mail address.
         *
         * Instead, send a temporary management link.
         */
        if (user.isVerified() && user.isActive()) {
            sendManagementLink(user);
            return genericEmailResponse();
        }

        /*
         * Important:
         *
         * For a pending or previously deactivated subscription, the newly
         * submitted topic selection replaces the old selection completely.
         *
         * Without this cleanup, historical inactive preferences could be
         * activated accidentally during verification.
         */
        List<UserPreferenceEntity> oldPreferences =
                preferencesFor(normalizedEmail);

        if (!oldPreferences.isEmpty()) {
            preferenceRepository.deleteAll(oldPreferences);
            preferenceRepository.flush();
        }

        for (Topic topic : parsedTopics) {
            preferenceRepository.save(
                    UserPreferenceEntity
                            .createPendingEmailSubscription(
                                    user,
                                    topic.name()
                            )
            );
        }

        String rawToken =
                tokenService.createVerificationToken(user);

        String verificationUrl =
                baseUrl + "/?verifyToken=" + rawToken;

        sendEmail(
                normalizedEmail,
                "WWF Polit-Assistant: E-Mail-Adresse bestätigen",
                """
                Guten Tag %s

                bitte bestätigen Sie Ihre E-Mail-Adresse, um Ihr WWF-Themen-Abonnement zu aktivieren.

                Bestätigungslink:
                %s

                Der Link ist 24 Stunden gültig.

                Falls Sie diese Anmeldung nicht angefordert haben, können Sie diese E-Mail ignorieren.

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        user.getDisplayName(),
                        verificationUrl
                )
        );

        return genericEmailResponse();
    }

    @Transactional
    public SubscriptionResponse verifySubscription(
            String rawToken
    ) {
        AppUserEntity user =
                tokenService.consumeVerificationToken(rawToken);

        user.verify();

        List<UserPreferenceEntity> preferences =
                preferencesFor(user.getEmail());

        preferences.forEach(
                UserPreferenceEntity::activate
        );

        List<String> activeTopics =
                preferences.stream()
                        .filter(UserPreferenceEntity::isActive)
                        .map(UserPreferenceEntity::getTopic)
                        .map(this::toDisplayName)
                        .distinct()
                        .sorted()
                        .toList();

        sendEmail(
                user.getEmail(),
                "WWF Polit-Assistant: Abonnement aktiviert",
                """
                Guten Tag %s

                Ihr WWF-Themen-Abonnement wurde erfolgreich aktiviert.

                E-Mail-Adresse:
                %s

                Abonnierte Themen:
                %s

                Sie erhalten künftig Benachrichtigungen zu neuen relevanten parlamentarischen Entwicklungen.

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        user.getDisplayName(),
                        user.getEmail(),
                        topicList(activeTopics)
                )
        );

        return new SubscriptionResponse(
                user.getEmail(),
                user.getDisplayName(),
                activeTopics
        );
    }

    @Transactional
    public ActionResponse requestManagementLink(
            String email
    ) {
        String normalizedEmail =
                normalizeEmail(email);

        userRepository
                .findByEmail(normalizedEmail)
                .filter(AppUserEntity::isVerified)
                .filter(AppUserEntity::isActive)
                .ifPresent(this::sendManagementLink);

        /*
         * Always return the same response.
         * This prevents disclosure of registered e-mail addresses.
         */
        return genericEmailResponse();
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(
            String rawToken
    ) {
        AppUserEntity user =
                tokenService
                        .validateManagementToken(rawToken)
                        .getUser();

        ensureActiveUser(user);

        List<String> topics =
                activeTopicsFor(user.getEmail());

        return new SubscriptionResponse(
                user.getEmail(),
                user.getDisplayName(),
                topics
        );
    }

    @Transactional
    public SubscriptionResponse updateSubscription(
            String rawToken,
            List<String> topics
    ) {
        AppUserEntity user =
                tokenService
                        .validateManagementToken(rawToken)
                        .getUser();

        ensureActiveUser(user);

        List<Topic> parsedTopics =
                parseTopics(topics);

        List<UserPreferenceEntity> existingPreferences =
                preferencesFor(user.getEmail());

        existingPreferences.forEach(
                UserPreferenceEntity::deactivate
        );

        for (Topic topic : parsedTopics) {
            UserPreferenceEntity preference =
                    existingPreferences.stream()
                            .filter(existing ->
                                    existing.getTopic()
                                            .equals(topic.name())
                            )
                            .findFirst()
                            .orElseGet(() ->
                                    UserPreferenceEntity
                                            .createEmailSubscription(
                                                    user,
                                                    topic.name()
                                            )
                            );

            preference.activate();

            if (!existingPreferences.contains(preference)) {
                preferenceRepository.save(preference);
            }
        }

        List<String> activeTopics =
                activeTopicsFor(user.getEmail());

        sendEmail(
                user.getEmail(),
                "WWF Polit-Assistant: Abonnement aktualisiert",
                """
                Guten Tag %s

                Ihr WWF-Themen-Abonnement wurde aktualisiert.

                Aktuell abonnierte Themen:
                %s

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        user.getDisplayName(),
                        topicList(activeTopics)
                )
        );

        return new SubscriptionResponse(
                user.getEmail(),
                user.getDisplayName(),
                activeTopics
        );
    }

    @Transactional
    public ActionResponse deleteSubscription(
            String rawToken
    ) {
        AppUserEntity user =
                tokenService
                        .validateManagementToken(rawToken)
                        .getUser();

        ensureActiveUser(user);

        preferencesFor(user.getEmail())
                .forEach(UserPreferenceEntity::deactivate);

        user.deactivate();

        sendEmail(
                user.getEmail(),
                "WWF Polit-Assistant: Abonnement beendet",
                """
                Guten Tag %s

                Ihr WWF-Themen-Abonnement wurde beendet.

                Sie erhalten keine weiteren Benachrichtigungen für dieses Abonnement.

                Sie können sich jederzeit erneut über den WWF Polit-Assistant anmelden.

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        user.getDisplayName()
                )
        );

        return new ActionResponse(
                "Das Abonnement wurde erfolgreich beendet."
        );
    }

    private void sendManagementLink(
            AppUserEntity user
    ) {
        String rawToken =
                tokenService.createManagementToken(user);

        String managementUrl =
                baseUrl + "/?manageToken=" + rawToken;

        sendEmail(
                user.getEmail(),
                "WWF Polit-Assistant: Abonnement verwalten",
                """
                Guten Tag %s

                über folgenden Link können Sie Ihr WWF-Themen-Abonnement verwalten:

                %s

                Der Link ist 60 Minuten gültig.

                Über diesen Link können Sie Ihre Themen aktualisieren oder Ihr Abonnement beenden.

                Falls Sie diesen Link nicht angefordert haben, können Sie diese E-Mail ignorieren.

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        user.getDisplayName(),
                        managementUrl
                )
        );
    }

    private List<UserPreferenceEntity> preferencesFor(
            String email
    ) {
        return preferenceRepository
                .findAll()
                .stream()
                .filter(preference ->
                        preference.getUser() != null
                                && preference.getUser()
                                .getEmail()
                                .equalsIgnoreCase(email)
                )
                .filter(preference ->
                        NotificationChannel.EMAIL.name()
                                .equals(preference.getChannel())
                )
                .toList();
    }

    private List<String> activeTopicsFor(
            String email
    ) {
        return preferencesFor(email)
                .stream()
                .filter(UserPreferenceEntity::isActive)
                .map(UserPreferenceEntity::getTopic)
                .map(this::toDisplayName)
                .distinct()
                .sorted()
                .toList();
    }

    private List<Topic> parseTopics(
            List<String> topics
    ) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mindestens ein WWF-Thema muss ausgewählt werden."
            );
        }

        return topics.stream()
                .map(topic -> {
                    if (topic == null || topic.isBlank()) {
                        throw new IllegalArgumentException(
                                "Ein WWF-Thema darf nicht leer sein."
                        );
                    }

                    return Topic.fromDisplayName(
                            topic.trim()
                    );
                })
                .peek(topic -> {
                    if (topic == Topic.SONSTIGES) {
                        throw new IllegalArgumentException(
                                "Das Thema 'Sonstiges' kann nicht abonniert werden."
                        );
                    }
                })
                .distinct()
                .toList();
    }

    private String normalizeEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Die E-Mail-Adresse darf nicht leer sein."
            );
        }

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private void ensureActiveUser(
            AppUserEntity user
    ) {
        if (!user.isVerified() || !user.isActive()) {
            throw new IllegalArgumentException(
                    "Das Abonnement ist nicht aktiv."
            );
        }
    }

    private String toDisplayName(
            String topicName
    ) {
        try {
            return Topic
                    .valueOf(topicName)
                    .getDisplayName();
        } catch (IllegalArgumentException exception) {
            return topicName;
        }
    }

    private String topicList(
            List<String> topics
    ) {
        if (topics == null || topics.isEmpty()) {
            return "-";
        }

        return topics.stream()
                .map(topic -> "- " + topic)
                .reduce(
                        (left, right) ->
                                left + System.lineSeparator() + right
                )
                .orElse("-");
    }

    private ActionResponse genericEmailResponse() {
        return new ActionResponse(
                "Falls für diese E-Mail-Adresse ein entsprechender Vorgang möglich ist, erhalten Sie eine E-Mail mit den nächsten Schritten."
        );
    }

    private void sendEmail(
            String recipient,
            String title,
            String message
    ) {
        notificationSender.send(
                new Notification(
                        null,
                        recipient,
                        NotificationChannel.EMAIL,
                        null,
                        title,
                        message
                )
        );
    }

    private String normalizeBaseUrl(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8080";
        }

        String normalized = value.trim();

        while (normalized.endsWith("/")) {
            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }

    public record SubscriptionResponse(
            String email,
            String displayName,
            List<String> topics
    ) {
    }

    public record ActionResponse(
            String message
    ) {
    }
}