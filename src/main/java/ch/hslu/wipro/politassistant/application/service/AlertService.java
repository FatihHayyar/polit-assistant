package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private final AlertJpaRepository alertRepository;
    private final AffairReadJpaRepository affairRepository;
    private final NotificationService notificationService;
    private final UserPreferenceJpaRepository userPreferenceRepository;

    public AlertService(
            AlertJpaRepository alertRepository,
            AffairReadJpaRepository affairRepository, NotificationService notificationService, UserPreferenceJpaRepository userPreferenceRepository
    ) {
        this.alertRepository = alertRepository;
        this.affairRepository = affairRepository;
        this.notificationService = notificationService;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Transactional
    public int createAlertsForImportedAffairs(List<Long> affairIds) {
        int created = 0;

        for (Long affairId : affairIds) {
            var results = affairRepository.findSummaryById(affairId);

            for (var affair : results) {
                if (affair.topic() == null || affair.topic().equals("OTHER")) {
                    continue;
                }

                var preferences = userPreferenceRepository.findByTopicAndActiveTrue(affair.topic());

                for (var preference : preferences) {
                    boolean exists = alertRepository.existsByAffairIdAndTopicAndChannelAndRecipientEmail(
                            affair.id(),
                            affair.topic(),
                            preference.getChannel(),
                            preference.getEmail()
                    );

                    if (exists) {
                        continue;
                    }

                    String title = "New political affair: " + affair.topic();

                    String message = """
                        Topic: %s
                        Title: %s
                        Type: %s
                        State: %s
                        Link: %s
                        Recipient: %s
                        """.formatted(
                            affair.topic(),
                            affair.title(),
                            affair.type(),
                            affair.state(),
                            affair.urlExternal(),
                            preference.getEmail()
                    );

                    alertRepository.save(AlertEntity.pending(
                            affair.id(),
                            affair.topic(),
                            preference.getChannel(),
                            preference.getEmail(),
                            title,
                            message
                    ));

                    created++;
                }
            }
        }

        return created;
    }
    @Transactional
    public int sendPendingAlerts() {
        var alerts = alertRepository.findByStatusInAndRetryCountLessThan(
                List.of("PENDING", "FAILED"),
                3
        );

        int sent = 0;

        for (var alert : alerts) {
            try {
                notificationService.notify(alert);
                alert.markSent();
                sent++;
            } catch (Exception e) {
                alert.markFailed(e.getMessage());
            }
        }

        return sent;
    }
}