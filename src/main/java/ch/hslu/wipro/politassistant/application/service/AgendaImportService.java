package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAgendasResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.agenda.AgendaJdbcRepository;
import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;
import org.springframework.stereotype.Service;

@Service
public class AgendaImportService {

    private final OpenParlDataClient client;
    private final AgendaJdbcRepository repository;
    private final AffairStorePort affairStorePort;
    private final AffairImportService affairImportService;

    public AgendaImportService(
            OpenParlDataClient client,
            AgendaJdbcRepository repository, AffairStorePort affairStorePort, AffairImportService affairImportService
    ) {
        this.client = client;
        this.repository = repository;
        this.affairStorePort = affairStorePort;
        this.affairImportService = affairImportService;
    }

    public int importAgendasForMeeting(Long meetingId) {
        OpenParlDataAgendasResponse response =
                client.fetchAgendasForMeeting(meetingId);

        if (response == null || response.data() == null) {
            return 0;
        }

        for (var agenda : response.data()) {

            repository.upsert(agenda);

            Long affairId = agenda.item_affair_id();

            if (affairId != null && !affairStorePort.existsById(affairId)) {
                affairImportService.importAffairWithDocsById(affairId);
            }
        }

        return response.data().size();
    }
}