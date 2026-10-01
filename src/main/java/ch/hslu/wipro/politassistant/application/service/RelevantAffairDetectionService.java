package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelevantAffairDetectionService {

    private final AffairReadJpaRepository affairRepository;

    public RelevantAffairDetectionService(
            AffairReadJpaRepository affairRepository
    ) {
        this.affairRepository = affairRepository;
    }

    public int countRelevantAffairs(List<Long> affairIds) {
        int relevant = 0;

        for (Long affairId : affairIds) {
            var classifications = affairRepository.findSummaryById(affairId);

            boolean isRelevant = classifications.stream()
                    .anyMatch(affair ->
                            affair.topic() != null
                                    && !affair.topic().equals("SONSTIGES")
                    );

            if (isRelevant) {
                relevant++;
            }
        }

        return relevant;
    }
}