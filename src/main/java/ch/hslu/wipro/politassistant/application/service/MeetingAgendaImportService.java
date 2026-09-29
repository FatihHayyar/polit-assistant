package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.meeting.MeetingJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingAgendaImportService {

    private final MeetingImportService meetingImportService;
    private final AgendaImportService agendaImportService;
    private final OpenParlDataClient client;
    private final MeetingJdbcRepository meetingRepository;

    public MeetingAgendaImportService(
            MeetingImportService meetingImportService,
            AgendaImportService agendaImportService, OpenParlDataClient client, MeetingJdbcRepository meetingRepository
    ) {
        this.meetingImportService = meetingImportService;
        this.agendaImportService = agendaImportService;
        this.client = client;
        this.meetingRepository = meetingRepository;
    }

    public int importMeetingWithAgendas(Long meetingId) {
        boolean meetingImported =
                meetingImportService.importMeetingById(meetingId);

        if (!meetingImported) {
            return 0;
        }

        return agendaImportService.importAgendasForMeeting(meetingId);
    }
    public ImportResult importMeetingPageWithAgendas(int offset, int limit) {

        OpenParlDataMeetingsResponse response =
                client.fetchMeetings(offset, limit, "-updated_at");

        if (response == null
                || response.data() == null
                || response.data().isEmpty()) {

            return new ImportResult(0, 0, null);
        }

        int importedMeetings = 0;
        int importedAgendas = 0;
        java.time.LocalDateTime maxUpdatedAt = null;

        for (OpenParlDataMeetingsResponse.MeetingDto meeting : response.data()) {

            meetingRepository.upsert(meeting);
            importedMeetings++;

            importedAgendas +=
                    agendaImportService.importAgendasForMeeting(meeting.id());

            if (meeting.updated_at() != null) {
                var updatedAt =
                        java.time.LocalDateTime.parse(meeting.updated_at());

                if (maxUpdatedAt == null || updatedAt.isAfter(maxUpdatedAt)) {
                    maxUpdatedAt = updatedAt;
                }
            }
        }

        return new ImportResult(
                importedMeetings,
                importedAgendas,
                maxUpdatedAt
        );
    }
    public ImportResult importMeetingsWithAgendas(
            int offset,
            int limit,
            java.time.LocalDateTime lastSync
    ) {
        int currentOffset = offset;
        int importedMeetings = 0;
        int importedAgendas = 0;
        java.time.LocalDateTime maxUpdatedAt = null;

        boolean reachedLastSync = false;

        while (true) {

            OpenParlDataMeetingsResponse response =
                    client.fetchMeetings(
                            currentOffset,
                            limit,
                            "-updated_at"
                    );

            if (response == null
                    || response.data() == null
                    || response.data().isEmpty()) {
                break;
            }

            for (OpenParlDataMeetingsResponse.MeetingDto meeting : response.data()) {

                java.time.LocalDateTime updatedAt = null;

                if (meeting.updated_at() != null) {
                    updatedAt = java.time.LocalDateTime.parse(
                            meeting.updated_at()
                    );
                }

                if (lastSync != null
                        && updatedAt != null
                        && !updatedAt.isAfter(lastSync)) {

                    reachedLastSync = true;
                    break;
                }

                meetingRepository.upsert(meeting);
                importedMeetings++;

                importedAgendas +=
                        agendaImportService.importAgendasForMeeting(
                                meeting.id()
                        );

                if (updatedAt != null
                        && (maxUpdatedAt == null
                        || updatedAt.isAfter(maxUpdatedAt))) {

                    maxUpdatedAt = updatedAt;
                }
            }

            // sorted by -updated_at:
            // once lastSync is reached, older pages are irrelevant
            if (reachedLastSync) {
                break;
            }

            if (response.meta() == null
                    || !response.meta().has_more()) {
                break;
            }

            currentOffset += limit;
        }

        return new ImportResult(
                importedMeetings,
                importedAgendas,
                maxUpdatedAt
        );
    }

    public record ImportResult(
            int importedMeetings,
            int importedAgendas,
            java.time.LocalDateTime maxUpdatedAt
    ) {}
}