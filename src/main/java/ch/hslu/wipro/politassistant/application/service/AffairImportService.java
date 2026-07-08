package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.mapper.OpenParlDataAffairMapper;
import ch.hslu.wipro.politassistant.adapter.out.persistence.RawAffairJdbcRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairDocJdbcRepository;
import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AffairImportService {

    private final OpenParlDataClient openParlDataClient;
    private final RawAffairJdbcRepository rawRepository;
    private final AffairStorePort affairStorePort;
    private final OpenParlDataAffairMapper mapper;
    private final AffairDocJdbcRepository docRepository;
    private final RuleBasedClassificationService classificationService;
    private final AlertService alertService;
    public AffairImportService(
            OpenParlDataClient openParlDataClient,
            RawAffairJdbcRepository rawRepository,
            AffairStorePort affairStorePort,
            OpenParlDataAffairMapper mapper,
            AffairDocJdbcRepository docRepository,
            RuleBasedClassificationService classificationService, AlertService alertService
    ) {
        this.openParlDataClient = openParlDataClient;
        this.rawRepository = rawRepository;
        this.affairStorePort = affairStorePort;
        this.mapper = mapper;
        this.docRepository = docRepository;
        this.classificationService = classificationService;
        this.alertService = alertService;
    }

    @Transactional
    public int importAffairs(int offset, int limit) {
        var response = openParlDataClient.fetchAffairs(offset, limit);

        if (response == null || response.data() == null) {
            return 0;
        }

        for (var dto : response.data()) {
            rawRepository.upsert(dto.id(), dto);
            affairStorePort.upsert(mapper.toDomain(dto));
        }

        return response.data().size();
    }

    @Transactional
    public int importDocsForAffair(Long affairId) {
        var response = openParlDataClient.fetchDocsForAffair(affairId);

        if (response == null || response.data() == null) {
            return 0;
        }

        for (var doc : response.data()) {
            docRepository.upsert(doc);
        }

        return response.data().size();
    }

    @Transactional
    public FullImportResult importAffairsWithDocs(int offset, int limit) {
        var response = openParlDataClient.fetchAffairs(offset, limit);

        if (response == null || response.data() == null) {
            return new FullImportResult(0, 0, 0, 0);
        }

        int affairsImported = 0;
        int docsImported = 0;
        var importedAffairIds = new ArrayList<Long>();

        for (var dto : response.data()) {
            rawRepository.upsert(dto.id(), dto);
            affairStorePort.upsert(mapper.toDomain(dto));
            affairsImported++;
            importedAffairIds.add(dto.id());

            var docsResponse = openParlDataClient.fetchDocsForAffair(dto.id());

            if (docsResponse != null && docsResponse.data() != null) {
                for (var doc : docsResponse.data()) {
                    docRepository.upsert(doc);
                    docsImported++;
                }
            }
        }

        int classified = classificationService.classifyAll(importedAffairIds);
        int alertsCreated = alertService.createAlertsForImportedAffairs(importedAffairIds);

        return new FullImportResult(affairsImported, docsImported, classified, alertsCreated);
    }

    public record FullImportResult(
            int affairsImported,
            int docsImported,
            int classified,
            int alertsCreated
    ) {}
}