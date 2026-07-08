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

    public AlertService(
            AlertJpaRepository alertRepository,
            AffairReadJpaRepository affairRepository
    ) {
        this.alertRepository = alertRepository;
        this.affairRepository = affairRepository;
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
}