package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse;
import ch.hslu.wipro.politassistant.application.service.*;
import org.springframework.web.bind.annotation.*;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAgendasResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;

import java.util.Map;

@RestController
class OpenParlDataTestController {

    private final OpenParlDataClient client;
    private final MeetingImportService meetingImportService;
    private final AgendaImportService agendaImportService;
    private final MeetingAgendaImportService meetingAgendaImportService;
    private final ImportOrchestratorService importOrchestratorService;
    private final AffairImportService affairImportService;

    OpenParlDataTestController(OpenParlDataClient client, MeetingImportService meetingImportService, AgendaImportService agendaImportService, MeetingAgendaImportService meetingAgendaImportService, ImportOrchestratorService importOrchestratorService, AffairImportService affairImportService) {
        this.client = client;
        this.meetingImportService = meetingImportService;
        this.agendaImportService = agendaImportService;
        this.meetingAgendaImportService = meetingAgendaImportService;
        this.importOrchestratorService = importOrchestratorService;
        this.affairImportService = affairImportService;
    }

    @GetMapping("/api/v1/dev/openparldata/affairs")
    OpenParlDataAffairsResponse testAffairs() {
        return client.fetchAffairs(0, 5);
    }
    @GetMapping("/api/v1/dev/openparldata/meetings")
    OpenParlDataMeetingsResponse testMeetings() {
        return client.fetchMeetings(0, 1);
    }

    @GetMapping("/api/v1/dev/openparldata/meetings/{id}/agendas")
    OpenParlDataAgendasResponse testMeetingAgendas(@PathVariable Long id) {
        return client.fetchAgendasForMeeting(id);
    }
    @PostMapping("/api/v1/dev/openparldata/meetings/import")
    public Map<String, Object> importMeetings(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        int imported = meetingImportService.importMeetings(offset, limit);

        return Map.of(
                "imported", imported,
                "offset", offset,
                "limit", limit
        );
    }
    @PostMapping("/api/v1/dev/openparldata/meetings/{id}/agendas/import")
    public Map<String, Object> importAgendas(@PathVariable Long id) {
        int imported = agendaImportService.importAgendasForMeeting(id);

        return Map.of(
                "meetingId", id,
                "imported", imported
        );
    }
    @PostMapping("/api/v1/dev/openparldata/meetings/{id}/import-with-agendas")
    public Map<String, Object> importMeetingWithAgendas(@PathVariable Long id) {

        int importedAgendas =
                meetingAgendaImportService.importMeetingWithAgendas(id);

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
                meetingAgendaImportService.importMeetingPageWithAgendas(
                        offset,
                        limit
                );

        return Map.of(
                "importedMeetings", result.importedMeetings(),
                "importedAgendas", result.importedAgendas(),
                "offset", offset,
                "limit", limit
        );
    }
    @PostMapping("/meetings/incremental")
    public MeetingAgendaImportService.ImportResult importMeetingsIncremental(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return importOrchestratorService.runMeetingAgendaImport(limit);
    }
    @PostMapping("/affairs/{id}/import-with-docs")
    public Map<String, Object> importAffairWithDocs(
            @PathVariable Long id
    ) {
        Long importedId = affairImportService.importAffairWithDocsById(id);

        return Map.of(
                "affairId", id,
                "imported", importedId != null
        );
    }
    @GetMapping("/api/v1/dev/openparldata/affairs/latest")
    OpenParlDataAffairsResponse latestAffairs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return client.fetchAffairs(offset, limit, "-updated_at");
    }
}