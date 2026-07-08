package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertService {

    private final AlertJpaRepository alertRepository;
    private final AffairReadJpaRepository affairRepository;
    private final NotificationService notificationService;
    public AlertService(
            AlertJpaRepository alertRepository,
            AffairReadJpaRepository affairRepository, NotificationService notificationService
    ) {
        this.alertRepository = alertRepository;
        this.affairRepository = affairRepository;
        this.notificationService = notificationService;
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

                boolean exists = alertRepository.existsByAffairIdAndTopicAndChannel(
                        affair.id(),
                        affair.topic(),
                        "TEAMS"
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
                        """.formatted(
                        affair.topic(),
                        affair.title(),
                        affair.type(),
                        affair.state(),
                        affair.urlExternal()
                );

                alertRepository.save(AlertEntity.pending(
                        affair.id(),
                        affair.topic(),
                        title,
                        message
                ));

                created++;
            }
        }

        return created;
    }
    @Transactional
    public int sendPendingAlerts() {
        var pendingAlerts = alertRepository.findByStatus("PENDING");

        int sent = 0;

        for (var alert : pendingAlerts) {
            notificationService.notify(alert);
            alert.markSent();
            sent++;
        }

        return sent;
    }
}