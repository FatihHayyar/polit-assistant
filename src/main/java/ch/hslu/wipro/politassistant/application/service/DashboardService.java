package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.user.UserPreferenceJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final AffairReadJpaRepository affairRepository;
    private final AlertJpaRepository alertRepository;
    private final UserPreferenceJpaRepository preferenceRepository;

    public DashboardService(
            AffairReadJpaRepository affairRepository,
            AlertJpaRepository alertRepository,
            UserPreferenceJpaRepository preferenceRepository
    ) {
        this.affairRepository = affairRepository;
        this.alertRepository = alertRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        return new DashboardSummary(
                affairRepository.count(),
                alertRepository.count(),
                alertRepository.countByStatus("PENDING"),
                alertRepository.countByStatus("SENT"),
                alertRepository.countByStatus("FAILED"),
                preferenceRepository.countByActiveTrue()
        );
    }

    public record DashboardSummary(
            long affairs,
            long alerts,
            long pendingAlerts,
            long sentAlerts,
            long failedAlerts,
            long activePreferences
    ) {}
}