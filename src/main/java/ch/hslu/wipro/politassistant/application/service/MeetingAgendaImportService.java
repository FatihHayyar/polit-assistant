package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.meeting.MeetingJdbcRepository;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingAgendaImportService {

    private final MeetingImportService meetingImportService;
    private final AgendaImportService agendaImportService;
    private final OpenParlDataClient client;
    private final MeetingJdbcRepository meetingRepository;

    public MeetingAgendaImportService(
            MeetingImportService meetingImportService,
            AgendaImportService agendaImportService,
            OpenParlDataClient client,
            MeetingJdbcRepository meetingRepository
    ) {
        this.meetingImportService = meetingImportService;
        this.agendaImportService = agendaImportService;
        this.client = client;
        this.meetingRepository = meetingRepository;
    }

    public int importMeetingWithAgendas(
            Long meetingId
    ) {
        boolean meetingImported =
                meetingImportService.importMeetingById(
                        meetingId
                );

        if (!meetingImported) {
            return 0;
        }

        return agendaImportService.importAgendasForMeeting(
                meetingId
        );
    }

    public ImportResult importMeetingPageWithAgendas(
            int offset,
            int limit
    ) {
        OpenParlDataMeetingsResponse response =
                client.fetchMeetings(
                        offset,
                        limit,
                        "-updated_at"
                );

        if (response == null
                || response.data() == null
                || response.data().isEmpty()) {

            return new ImportResult(
                    0,
                    0,
                    null,
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        int importedMeetings = 0;
        int importedAgendas = 0;
        LocalDateTime maxUpdatedAt = null;

        for (OpenParlDataMeetingsResponse.MeetingDto meeting
                : response.data()) {

            meetingRepository.upsert(meeting);
            importedMeetings++;

            importedAgendas +=
                    agendaImportService.importAgendasForMeeting(
                            meeting.id()
                    );

            if (meeting.updated_at() != null) {
                var updatedAt =
                        LocalDateTime.parse(
                                meeting.updated_at()
                        );

                if (maxUpdatedAt == null
                        || updatedAt.isAfter(maxUpdatedAt)) {

                    maxUpdatedAt = updatedAt;
                }
            }
        }

        return new ImportResult(
                importedMeetings,
                importedAgendas,
                maxUpdatedAt,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public ImportResult importMeetingsWithAgendas(
            int offset,
            int limit,
            LocalDateTime lastSync
    ) {
        int currentOffset = offset;
        int importedMeetings = 0;
        int importedAgendas = 0;

        LocalDateTime maxUpdatedAt = null;

        boolean reachedLastSync = false;

        List<AlertEvent> events =
                new ArrayList<>();

        List<Long> importedMissingAffairIds =
                new ArrayList<>();

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

            for (OpenParlDataMeetingsResponse.MeetingDto meeting
                    : response.data()) {

                LocalDateTime updatedAt = null;

                if (meeting.updated_at() != null) {
                    updatedAt =
                            LocalDateTime.parse(
                                    meeting.updated_at()
                            );
                }

                /*
                 * Meetings are sorted by -updated_at.
                 * Once the previous checkpoint is reached, all following
                 * meetings are older and do not need to be processed.
                 */
                if (lastSync != null
                        && updatedAt != null
                        && !updatedAt.isAfter(lastSync)) {

                    reachedLastSync = true;
                    break;
                }

                meetingRepository.upsert(meeting);
                importedMeetings++;

                var agendaResult =
                        agendaImportService
                                .importAgendasForMeetingIncremental(
                                        meeting.id()
                                );

                importedAgendas +=
                        agendaResult.importedAgendas();

                events.addAll(
                        agendaResult.events()
                );

                importedMissingAffairIds.addAll(
                        agendaResult.importedMissingAffairIds()
                );

                if (updatedAt != null
                        && (maxUpdatedAt == null
                        || updatedAt.isAfter(maxUpdatedAt))) {

                    maxUpdatedAt = updatedAt;
                }
            }

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
                maxUpdatedAt,
                events,
                importedMissingAffairIds
        );
    }

    public record ImportResult(
            int importedMeetings,
            int importedAgendas,
            LocalDateTime maxUpdatedAt,
            List<AlertEvent> events,
            List<Long> importedMissingAffairIds
    ) {
    }
}