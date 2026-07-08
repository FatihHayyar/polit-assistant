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


    public AffairImportService(
            OpenParlDataClient openParlDataClient,
            RawAffairJdbcRepository rawRepository,
            AffairStorePort affairStorePort,
            OpenParlDataAffairMapper mapper,
            AffairDocJdbcRepository docRepository

    ) {
        this.openParlDataClient = openParlDataClient;
        this.rawRepository = rawRepository;
        this.affairStorePort = affairStorePort;
        this.mapper = mapper;
        this.docRepository = docRepository;
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
    public ImportOrchestratorService.ImportResult importAffairsWithDocsOnly(int offset, int limit) {
        java.time.LocalDateTime maxUpdatedAt = null;
        var response = openParlDataClient.fetchAffairs(offset, limit);
        if (response == null || response.data() == null) {
            return new ImportOrchestratorService.ImportResult(
                    0,
                    0,
                    new java.util.ArrayList<>(),
                    null
            );
        }

        int affairsImported = 0;
        int docsImported = 0;
        var importedAffairIds = new java.util.ArrayList<Long>();

        for (var dto : response.data()) {
            rawRepository.upsert(dto.id(), dto);
            affairStorePort.upsert(mapper.toDomain(dto));
            affairsImported++;
            importedAffairIds.add(dto.id());
            var updatedAt = java.time.LocalDateTime.parse(dto.updated_at());
            if (maxUpdatedAt == null || updatedAt.isAfter(maxUpdatedAt)) {
                maxUpdatedAt = updatedAt;
            }
            var docsResponse = openParlDataClient.fetchDocsForAffair(dto.id());

            if (docsResponse != null && docsResponse.data() != null) {
                for (var doc : docsResponse.data()) {
                    docRepository.upsert(doc);
                    docsImported++;
                }
            }
        }

        return new ImportOrchestratorService.ImportResult(
                affairsImported,
                docsImported,
                importedAffairIds,
                maxUpdatedAt
        );
    }
    @Transactional
    public ImportOrchestratorService.ImportResult importLatestAffairsOnly(
            int limit,
            java.time.LocalDateTime lastSync
    ) {
        var response = openParlDataClient.fetchLatestAffairs(limit);

        if (response == null || response.data() == null) {
            return new ImportOrchestratorService.ImportResult(
                    0,
                    0,
                    new java.util.ArrayList<>(),
                    null
            );
        }

        int affairsImported = 0;
        int docsImported = 0;
        var importedAffairIds = new java.util.ArrayList<Long>();
        java.time.LocalDateTime maxUpdatedAt = null;

        for (var dto : response.data()) {
            if (dto.updated_at() == null || dto.updated_at().isBlank()) {
                continue;
            }

            var updatedAt = java.time.LocalDateTime.parse(dto.updated_at());

            if (lastSync != null && !updatedAt.isAfter(lastSync)) {
                break;
            }

            if (maxUpdatedAt == null || updatedAt.isAfter(maxUpdatedAt)) {
                maxUpdatedAt = updatedAt;
            }

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

        return new ImportOrchestratorService.ImportResult(
                affairsImported,
                docsImported,
                importedAffairIds,
                maxUpdatedAt
        );
    }
}