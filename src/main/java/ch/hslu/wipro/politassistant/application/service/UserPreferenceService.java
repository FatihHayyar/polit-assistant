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
            return new ActionResponse(
                    "Für diese E-Mail-Adresse besteht bereits ein aktives Abonnement."
            );
        }

        /*
         * A pending or previously deactivated legacy subscription may still
         * exist in the database. Its previous topic selection is replaced
         * completely before a new verification process is started.
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

        return new ActionResponse(
                "Eine Bestätigungs-E-Mail wurde gesendet. "
                        + "Bitte bestätigen Sie Ihre E-Mail-Adresse, um das Abonnement zu aktivieren."
        );
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

        AppUserEntity user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .filter(AppUserEntity::isVerified)
                        .filter(AppUserEntity::isActive)
                        .orElse(null);

        if (user == null) {
            return new ActionResponse(
                    "Für diese E-Mail-Adresse besteht kein aktives Abonnement."
            );
        }

        /*
         * An active user should also have at least one active EMAIL
         * preference. This prevents an inconsistent user record from being
         * treated as a valid subscription.
         */
        boolean hasActiveEmailPreference =
                preferenceRepository
                        .findByUserId(user.getId())
                        .stream()
                        .anyMatch(preference ->
                                preference.isActive()
                                        && NotificationChannel.EMAIL.name()
                                        .equals(preference.getChannel())
                        );

        if (!hasActiveEmailPreference) {
            return new ActionResponse(
                    "Für diese E-Mail-Adresse besteht kein aktives Abonnement."
            );
        }

        sendManagementLink(user);

        return new ActionResponse(
                "Der Verwaltungslink wurde per E-Mail gesendet."
        );
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

        String email = user.getEmail();
        String displayName = user.getDisplayName();

        /*
         * user_preferences uses NO ACTION for its foreign key to app_users.
         * Therefore preferences must be physically removed first.
         */
        List<UserPreferenceEntity> preferences =
                preferenceRepository.findByUserId(
                        user.getId()
                );

        if (!preferences.isEmpty()) {
            preferenceRepository.deleteAll(preferences);
            preferenceRepository.flush();
        }

        /*
         * subscription_tokens uses ON DELETE CASCADE, therefore deleting
         * the user also removes all remaining verification/management tokens.
         */
        userRepository.delete(user);
        userRepository.flush();

        /*
         * The confirmation is sent using the values copied before deletion.
         * The user no longer needs to exist in the database for this mail.
         */
        sendEmail(
                email,
                "WWF Polit-Assistant: Abonnement gelöscht",
                """
                Guten Tag %s

                Ihr WWF-Themen-Abonnement wurde vollständig gelöscht.

                Ihre E-Mail-Adresse und Ihre Themenauswahl werden nicht mehr
                als aktives Abonnement im WWF Polit-Assistant geführt.

                Sie können sich jederzeit erneut anmelden. In diesem Fall
                wird ein neues Abonnement erstellt und Ihre E-Mail-Adresse
                erneut bestätigt.

                Freundliche Grüsse
                WWF Polit-Assistant
                """.formatted(
                        displayName
                )
        );

        return new ActionResponse(
                "Das Abonnement wurde vollständig gelöscht."
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

                Über diesen Link können Sie Ihre Themen aktualisieren oder Ihr Abonnement löschen.

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