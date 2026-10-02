package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import ch.hslu.wipro.politassistant.domain.alert.AlertEventType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private static final String EMAIL_CHANNEL = "EMAIL";

    private final AlertJpaRepository alertRepository;
    private final AffairReadJpaRepository affairRepository;
    private final NotificationService notificationService;
    private final UserPreferenceJpaRepository userPreferenceRepository;

    public AlertService(
            AlertJpaRepository alertRepository,
            AffairReadJpaRepository affairRepository,
            NotificationService notificationService,
            UserPreferenceJpaRepository userPreferenceRepository
    ) {
        this.alertRepository = alertRepository;
        this.affairRepository = affairRepository;
        this.notificationService = notificationService;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Transactional
    public int createAlertsForEvents(
            List<AlertEvent> events
    ) {
        if (events == null || events.isEmpty()) {
            return 0;
        }

        int created = 0;

        for (AlertEvent event : events) {
            created += createAlertsForEvent(event);
        }

        return created;
    }

    private int createAlertsForEvent(
            AlertEvent event
    ) {
        int created = 0;

        var results =
                affairRepository.findSummaryById(
                        event.affairId()
                );

        for (var affair : results) {

            if (affair.topic() == null
                    || "SONSTIGES".equals(affair.topic())) {
                continue;
            }

            /*
             * TOPIC_NEW belongs only to the newly acquired topic.
             *
             * All other event types apply to every currently relevant
             * WWF topic of the affair.
             */
            if (event.type() == AlertEventType.TOPIC_NEW) {
                if (event.targetTopic() == null
                        || !event.targetTopic().equals(
                        affair.topic()
                )) {
                    continue;
                }
            }

            var preferences =
                    userPreferenceRepository
                            .findByTopicAndActiveTrue(
                                    affair.topic()
                            );

            for (var preference : preferences) {

                boolean exists =
                        alertRepository
                                .existsByEventKeyAndTopicAndChannelAndRecipientEmail(
                                        event.eventKey(),
                                        affair.topic(),
                                        EMAIL_CHANNEL,
                                        preference.getEmail()
                                );

                if (exists) {
                    continue;
                }

                String topicDisplayName =
                        toTopicDisplayName(
                                affair.topic()
                        );

                String title =
                        buildTitle(
                                event.type(),
                                topicDisplayName
                        );

                String message =
                        buildMessage(
                                event.type(),
                                topicDisplayName,
                                affair.title(),
                                affair.type(),
                                affair.state(),
                                affair.urlExternal()
                        );

                alertRepository.save(
                        AlertEntity.pending(
                                affair.id(),
                                affair.topic(),
                                EMAIL_CHANNEL,
                                preference.getEmail(),
                                event.type(),
                                event.eventKey(),
                                title,
                                message
                        )
                );

                created++;
            }
        }

        return created;
    }

    @Transactional
    public int sendPendingAlerts() {
        var alerts =
                alertRepository
                        .findByStatusInAndRetryCountLessThan(
                                List.of(
                                        "PENDING",
                                        "FAILED"
                                ),
                                3
                        );

        int sent = 0;

        for (var alert : alerts) {
            try {
                notificationService.notify(
                        alert
                );

                alert.markSent();
                sent++;

            } catch (Exception e) {
                alert.markFailed(
                        e.getMessage()
                );
            }
        }

        return sent;
    }

    private String buildTitle(
            AlertEventType eventType,
            String topicDisplayName
    ) {
        return switch (eventType) {

            case AFFAIR_NEW ->
                    "Neues parlamentarisches Geschäft: "
                            + topicDisplayName;

            case DOCUMENT_NEW ->
                    "Neues Dokument zu einem relevanten Geschäft: "
                            + topicDisplayName;

            case TOPIC_NEW ->
                    "Neue WWF-Themenrelevanz erkannt: "
                            + topicDisplayName;

            case AGENDA_NEW ->
                    "Neues relevantes Traktandum: "
                            + topicDisplayName;

            case AGENDA_DATE_CHANGED ->
                    "Termin eines relevanten Traktandums aktualisiert: "
                            + topicDisplayName;
        };
    }

    private String buildMessage(
            AlertEventType eventType,
            String topicDisplayName,
            String affairTitle,
            String affairType,
            String affairState,
            String affairUrl
    ) {
        String eventDescription =
                switch (eventType) {

                    case AFFAIR_NEW ->
                            "Ein neues relevantes parlamentarisches Geschäft wurde erkannt.";

                    case DOCUMENT_NEW ->
                            "Zu einem relevanten parlamentarischen Geschäft wurde ein neues Dokument veröffentlicht.";

                    case TOPIC_NEW ->
                            "Für ein bestehendes parlamentarisches Geschäft wurde eine neue WWF-Themenrelevanz erkannt.";

                    case AGENDA_NEW ->
                            "Ein relevantes parlamentarisches Geschäft wurde neu traktandiert.";

                    case AGENDA_DATE_CHANGED ->
                            "Der Termin eines relevanten Traktandums wurde neu festgelegt oder geändert.";
                };

        return """
                WWF Polit-Assistant

                %s

                Thema: %s
                Titel: %s
                Typ: %s
                Status: %s
                Link: %s
                """.formatted(
                eventDescription,
                topicDisplayName,
                safe(affairTitle),
                safe(affairType),
                safe(affairState),
                safe(affairUrl)
        );
    }

    private String safe(
            String value
    ) {
        return value != null
                ? value
                : "-";
    }

    private String toTopicDisplayName(
            String databaseTopic
    ) {
        try {
            return ch.hslu.wipro.politassistant.domain.classification.Topic
                    .valueOf(databaseTopic)
                    .getDisplayName();

        } catch (IllegalArgumentException e) {
            return databaseTopic;
        }
    }
}