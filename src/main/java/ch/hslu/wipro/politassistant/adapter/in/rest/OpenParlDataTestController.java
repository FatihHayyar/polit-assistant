package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAgendasResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;
import ch.hslu.wipro.politassistant.application.service.AffairImportService;
import ch.hslu.wipro.politassistant.application.service.AgendaImportService;
import ch.hslu.wipro.politassistant.application.service.AlertService;
import ch.hslu.wipro.politassistant.application.service.MeetingAgendaImportService;
import ch.hslu.wipro.politassistant.application.service.MeetingImportService;
import ch.hslu.wipro.politassistant.application.service.UpdateWorkflowService;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import ch.hslu.wipro.politassistant.domain.alert.AlertEventType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
class OpenParlDataTestController {

    private final OpenParlDataClient client;
    private final MeetingImportService meetingImportService;
    private final AgendaImportService agendaImportService;
    private final MeetingAgendaImportService meetingAgendaImportService;
    private final AffairImportService affairImportService;
    private final UpdateWorkflowService updateWorkflowService;
    private final AlertService alertService;

    OpenParlDataTestController(
            OpenParlDataClient client,
            MeetingImportService meetingImportService,
            AgendaImportService agendaImportService,
            MeetingAgendaImportService meetingAgendaImportService,
            AffairImportService affairImportService,
            UpdateWorkflowService updateWorkflowService,
            AlertService alertService
    ) {
        this.client = client;
        this.meetingImportService = meetingImportService;
        this.agendaImportService = agendaImportService;
        this.meetingAgendaImportService = meetingAgendaImportService;
        this.affairImportService = affairImportService;
        this.updateWorkflowService = updateWorkflowService;
        this.alertService = alertService;
    }

    @GetMapping("/api/v1/dev/openparldata/affairs")
    OpenParlDataAffairsResponse testAffairs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return client.fetchAffairs(
                offset,
                limit
        );
    }

    @GetMapping("/api/v1/dev/openparldata/meetings")
    OpenParlDataMeetingsResponse testMeetings(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "1") int limit
    ) {
        return client.fetchMeetings(
                offset,
                limit
        );
    }

    @GetMapping("/api/v1/dev/openparldata/meetings/{id}/agendas")
    OpenParlDataAgendasResponse testMeetingAgendas(
            @PathVariable Long id
    ) {
        return client.fetchAgendasForMeeting(
                id
        );
    }

    @PostMapping("/api/v1/dev/openparldata/meetings/import")
    public Map<String, Object> importMeetings(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        int imported =
                meetingImportService.importMeetings(
                        offset,
                        limit
                );

        return Map.of(
                "imported", imported,
                "offset", offset,
                "limit", limit
        );
    }

    @PostMapping("/api/v1/dev/openparldata/meetings/{id}/agendas/import")
    public Map<String, Object> importAgendas(
            @PathVariable Long id
    ) {
        int imported =
                agendaImportService.importAgendasForMeeting(
                        id
                );

        return Map.of(
                "meetingId", id,
                "imported", imported
        );
    }

    @PostMapping("/api/v1/dev/openparldata/meetings/{id}/import-with-agendas")
    public Map<String, Object> importMeetingWithAgendas(
            @PathVariable Long id
    ) {
        int importedAgendas =
                meetingAgendaImportService
                        .importMeetingWithAgendas(
                                id
                        );

        return Map.of(
                "meetingId", id,
                "importedAgendas", importedAgendas
        );
    }

    @PostMapping("/api/v1/dev/openparldata/meetings/import-with-agendas")
    public Map<String, Object> importMeetingsWithAgendas(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        MeetingAgendaImportService.ImportResult result =
                meetingAgendaImportService
                        .importMeetingPageWithAgendas(
                                offset,
                                limit
                        );

        return Map.of(
                "importedMeetings",
                result.importedMeetings(),
                "importedAgendas",
                result.importedAgendas(),
                "offset",
                offset,
                "limit",
                limit
        );
    }

    @PostMapping("/api/v1/dev/openparldata/affairs/{id}/import-with-docs")
    public Map<String, Object> importAffairWithDocs(
            @PathVariable Long id
    ) {
        Long importedId =
                affairImportService
                        .importAffairWithDocsById(
                                id
                        );

        return Map.of(
                "affairId",
                id,
                "imported",
                importedId != null
        );
    }

    @GetMapping("/api/v1/dev/openparldata/affairs/latest")
    OpenParlDataAffairsResponse latestAffairs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return client.fetchAffairs(
                offset,
                limit,
                "-updated_at"
        );
    }

    /**
     * Manual operational update.
     *
     * Uses exactly the same workflow as the scheduler.
     */
    @PostMapping("/api/v1/openparldata/update")
    public UpdateWorkflowService.UpdateResult update(
            @RequestParam(defaultValue = "50") int affairLimit,
            @RequestParam(defaultValue = "50") int meetingLimit
    ) {
        return updateWorkflowService.runUpdate(
                affairLimit,
                meetingLimit
        );
    }

    /**
     * Development-only end-to-end alert test.
     *
     * Uses an existing classified affair but creates a unique
     * AFFAIR_NEW event so that the normal alert/subscription/email
     * pipeline can be tested without modifying parliamentary data.
     */
    @PostMapping("/api/v1/dev/alerts/test-new-affair/{id}")
    public Map<String, Object> testNewAffairAlert(
            @PathVariable Long id
    ) {
        String eventKey =
                "TEST:AFFAIR_NEW:"
                        + id
                        + ":"
                        + Instant.now().toEpochMilli();

        AlertEvent event =
                new AlertEvent(
                        AlertEventType.AFFAIR_NEW,
                        eventKey,
                        id,
                        null
                );

        int alertsCreated =
                alertService.createAlertsForEvents(
                        List.of(event)
                );

        int notificationsSent =
                alertService.sendPendingAlerts();

        return Map.of(
                "affairId", id,
                "eventType", AlertEventType.AFFAIR_NEW.name(),
                "eventKey", eventKey,
                "alertsCreated", alertsCreated,
                "notificationsSent", notificationsSent
        );
    }
}