package ch.hslu.wipro.politassistant.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateWorkflowService {

    private static final Logger log =
            LoggerFactory.getLogger(UpdateWorkflowService.class);

    private final ImportOrchestratorService orchestratorService;
    private final AlertService alertService;

    public UpdateWorkflowService(
            ImportOrchestratorService orchestratorService,
            AlertService alertService
    ) {
        this.orchestratorService = orchestratorService;
        this.alertService = alertService;
    }

    public UpdateResult runUpdate(
            int affairLimit,
            int meetingLimit
    ) {
        if (affairLimit <= 0) {
            throw new IllegalArgumentException(
                    "affairLimit must be > 0"
            );
        }

        if (meetingLimit <= 0) {
            throw new IllegalArgumentException(
                    "meetingLimit must be > 0"
            );
        }

        log.info(
                "OpenParlData update workflow started: affairLimit={}, meetingLimit={}",
                affairLimit,
                meetingLimit
        );

        var affairResult =
                orchestratorService.runIncrementalImport(
                        affairLimit
                );

        var meetingResult =
                orchestratorService.runMeetingAgendaImport(
                        meetingLimit
                );

        int notificationsSent =
                alertService.sendPendingAlerts();

        int agendaEvents =
                meetingResult.eventsDetected();

        int totalAlertsCreated =
                affairResult.alertsCreated()
                        + meetingResult.alertsCreated();

        boolean relevantChangesFound =
                totalAlertsCreated > 0
                        || agendaEvents > 0;

        String status =
                relevantChangesFound
                        ? "UPDATED"
                        : "UP_TO_DATE";

        String message =
                relevantChangesFound
                        ? "Aktualisierung erfolgreich abgeschlossen. Neue relevante Entwicklungen wurden verarbeitet."
                        : "Alles ist aktuell. Keine neuen relevanten Entwicklungen gefunden.";

        var result =
                new UpdateResult(
                        status,
                        message,
                        affairResult.affairsImported(),
                        affairResult.docsImported(),
                        affairResult.classified(),
                        affairResult.relevantAffairs(),
                        meetingResult.importedMeetings(),
                        meetingResult.importedAgendas(),
                        agendaEvents,
                        totalAlertsCreated,
                        notificationsSent
                );

        log.info(
                "OpenParlData update workflow completed: status={}, affairs={}, docs={}, meetings={}, agendas={}, agendaEvents={}, alertsCreated={}, notificationsSent={}",
                result.status(),
                result.affairsUpdated(),
                result.documentsImported(),
                result.meetingsUpdated(),
                result.agendasImported(),
                result.agendaEvents(),
                result.alertsCreated(),
                result.notificationsSent()
        );

        return result;
    }

    public record UpdateResult(
            String status,
            String message,
            int affairsUpdated,
            int documentsImported,
            int affairsClassified,
            int relevantAffairsProcessed,
            int meetingsUpdated,
            int agendasImported,
            int agendaEvents,
            int alertsCreated,
            int notificationsSent
    ) {
    }
}