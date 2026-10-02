package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.persistence.agenda.AgendaJdbcRepository;
import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AgendaImportService {

    private final OpenParlDataClient client;
    private final AgendaJdbcRepository repository;
    private final AffairStorePort affairStorePort;
    private final AffairImportService affairImportService;

    public AgendaImportService(
            OpenParlDataClient client,
            AgendaJdbcRepository repository,
            AffairStorePort affairStorePort,
            AffairImportService affairImportService
    ) {
        this.client = client;
        this.repository = repository;
        this.affairStorePort = affairStorePort;
        this.affairImportService = affairImportService;
    }

    /**
     * Baseline/manual import.
     *
     * This method intentionally does not create alert events.
     */
    public int importAgendasForMeeting(
            Long meetingId
    ) {
        var response =
                client.fetchAgendasForMeeting(
                        meetingId
                );

        if (response == null
                || response.data() == null) {
            return 0;
        }

        for (var agenda : response.data()) {
            repository.upsert(agenda);

            Long affairId =
                    agenda.item_affair_id();

            if (affairId != null
                    && !affairStorePort.existsById(affairId)) {

                affairImportService.importAffairWithDocsById(
                        affairId
                );
            }
        }

        return response.data().size();
    }

    /**
     * Operational/incremental agenda import.
     *
     * Detects only real changes:
     * - a new agenda linked to an affair
     * - an existing agenda receiving/changing its item date
     *
     * Pure re-imports of unchanged agendas produce no event.
     */
    public IncrementalAgendaResult importAgendasForMeetingIncremental(
            Long meetingId
    ) {
        var response =
                client.fetchAgendasForMeeting(
                        meetingId
                );

        if (response == null
                || response.data() == null) {

            return new IncrementalAgendaResult(
                    0,
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        List<AlertEvent> events =
                new ArrayList<>();

        List<Long> importedMissingAffairIds =
                new ArrayList<>();

        int importedAgendas = 0;

        for (var agenda : response.data()) {

            var previous =
                    repository.findSnapshot(
                            agenda.id()
                    );

            Long affairId =
                    agenda.item_affair_id();

            LocalDateTime newItemDate =
                    parseDate(
                            agenda.item_date()
                    );

            boolean affairAlreadyExists =
                    affairId != null
                            && affairStorePort.existsById(
                            affairId
                    );

            /*
             * Persist the newest agenda state first.
             */
            repository.upsert(agenda);
            importedAgendas++;

            /*
             * An agenda may reference an affair that has not yet been
             * imported by the normal affair update.
             */
            if (affairId != null
                    && !affairAlreadyExists) {

                Long importedAffairId =
                        affairImportService.importAffairWithDocsById(
                                affairId
                        );

                if (importedAffairId != null) {
                    importedMissingAffairIds.add(
                            importedAffairId
                    );
                }
            }

            /*
             * Agendas without a linked affair cannot be mapped to a WWF
             * topic and therefore cannot create a topic subscription alert.
             */
            if (affairId == null) {
                continue;
            }

            if (previous.isEmpty()) {
                events.add(
                        AlertEvent.newAgenda(
                                affairId,
                                agenda.id()
                        )
                );

                /*
                 * A completely new agenda creates AGENDA_NEW only.
                 * We intentionally do not also create DATE_CHANGED.
                 */
                continue;
            }

            LocalDateTime previousItemDate =
                    previous.get().itemDate();

            if (!Objects.equals(
                    previousItemDate,
                    newItemDate
            )) {
                /*
                 * We notify when a relevant date becomes known or changes.
                 * If a previously known date simply disappears, that is not
                 * currently treated as a positive date notification.
                 */
                if (newItemDate != null) {
                    events.add(
                            AlertEvent.agendaDateChanged(
                                    affairId,
                                    agenda.id(),
                                    newItemDate.toString()
                            )
                    );
                }
            }
        }

        return new IncrementalAgendaResult(
                importedAgendas,
                events,
                importedMissingAffairIds
        );
    }

    private LocalDateTime parseDate(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value);
    }

    public record IncrementalAgendaResult(
            int importedAgendas,
            List<AlertEvent> events,
            List<Long> importedMissingAffairIds
    ) {
    }
}