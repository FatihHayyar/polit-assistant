package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.mapper.OpenParlDataAffairMapper;
import ch.hslu.wipro.politassistant.adapter.out.persistence.RawAffairJdbcRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairDocJdbcRepository;
import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AffairImportService {

    private static final Logger log =
            LoggerFactory.getLogger(AffairImportService.class);

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
    public int importAffairs(
            int offset,
            int limit
    ) {
        var response =
                openParlDataClient.fetchAffairs(
                        offset,
                        limit
                );

        if (response == null || response.data() == null) {
            return 0;
        }

        for (var dto : response.data()) {
            rawRepository.upsert(
                    dto.id(),
                    dto
            );

            affairStorePort.upsert(
                    mapper.toDomain(dto)
            );
        }

        return response.data().size();
    }

    @Transactional
    public int importDocsForAffair(
            Long affairId
    ) {
        var response =
                openParlDataClient.fetchDocsForAffair(
                        affairId
                );

        if (response == null || response.data() == null) {
            return 0;
        }

        for (var doc : response.data()) {
            docRepository.upsert(doc);
        }

        return response.data().size();
    }

    @Transactional
    public ImportOrchestratorService.ImportResult importAffairsWithDocsOnly(
            int offset,
            int limit
    ) {
        LocalDateTime maxUpdatedAt = null;

        log.info(
                "Fetching affair page: offset={}, limit={}",
                offset,
                limit
        );

        var response =
                openParlDataClient.fetchAffairs(
                        offset,
                        limit
                );

        if (response == null
                || response.data() == null
                || response.data().isEmpty()) {

            log.info(
                    "No affairs returned for offset={}",
                    offset
            );

            return new ImportOrchestratorService.ImportResult(
                    0,
                    0,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    null
            );
        }

        int affairsImported = 0;
        int docsImported = 0;

        var importedAffairIds =
                new ArrayList<Long>();

        for (var dto : response.data()) {
            rawRepository.upsert(
                    dto.id(),
                    dto
            );

            affairStorePort.upsert(
                    mapper.toDomain(dto)
            );

            affairsImported++;
            importedAffairIds.add(dto.id());

            if (dto.updated_at() != null
                    && !dto.updated_at().isBlank()) {

                var updatedAt =
                        LocalDateTime.parse(
                                dto.updated_at()
                        );

                if (maxUpdatedAt == null
                        || updatedAt.isAfter(maxUpdatedAt)) {
                    maxUpdatedAt = updatedAt;
                }
            }

            try {
                var docsResponse =
                        openParlDataClient.fetchDocsForAffair(
                                dto.id()
                        );

                if (docsResponse != null
                        && docsResponse.data() != null) {

                    for (var doc : docsResponse.data()) {
                        docRepository.upsert(doc);
                        docsImported++;
                    }
                }

            } catch (Exception exception) {
                log.warn(
                        "Could not import documents for affairId={}. " +
                                "Affair import will continue. Reason: {}",
                        dto.id(),
                        exception.getMessage()
                );
            }
        }

        log.info(
                "Affair page completed: offset={}, affairs={}, docs={}",
                offset,
                affairsImported,
                docsImported
        );

        return new ImportOrchestratorService.ImportResult(
                affairsImported,
                docsImported,
                importedAffairIds,
                new ArrayList<>(),
                new ArrayList<>(),
                maxUpdatedAt
        );
    }

    @Transactional
    public ImportOrchestratorService.ImportResult importLatestAffairsOnly(
            int limit,
            LocalDateTime lastSync
    ) {
        int currentOffset = 0;
        int affairsImported = 0;
        int docsImported = 0;

        var importedAffairIds =
                new ArrayList<Long>();

        var newAffairIds =
                new ArrayList<Long>();

        var newDocumentEvents =
                new ArrayList<AlertEvent>();

        LocalDateTime maxUpdatedAt = null;
        boolean reachedLastSync = false;

        while (true) {
            var response =
                    openParlDataClient.fetchAffairs(
                            currentOffset,
                            limit,
                            "-updated_at"
                    );

            if (response == null
                    || response.data() == null
                    || response.data().isEmpty()) {
                break;
            }

            for (var dto : response.data()) {
                if (dto.updated_at() == null
                        || dto.updated_at().isBlank()) {
                    continue;
                }

                var updatedAt =
                        LocalDateTime.parse(
                                dto.updated_at()
                        );

                if (lastSync != null
                        && !updatedAt.isAfter(lastSync)) {
                    reachedLastSync = true;
                    break;
                }

                if (maxUpdatedAt == null
                        || updatedAt.isAfter(maxUpdatedAt)) {
                    maxUpdatedAt = updatedAt;
                }

                boolean affairAlreadyExists =
                        affairStorePort.existsById(dto.id());

                rawRepository.upsert(
                        dto.id(),
                        dto
                );

                affairStorePort.upsert(
                        mapper.toDomain(dto)
                );

                affairsImported++;
                importedAffairIds.add(dto.id());

                if (!affairAlreadyExists) {
                    newAffairIds.add(dto.id());
                }

                try {
                    var docsResponse =
                            openParlDataClient.fetchDocsForAffair(
                                    dto.id()
                            );

                    if (docsResponse != null
                            && docsResponse.data() != null
                            && !docsResponse.data().isEmpty()) {

                        List<Long> documentIds =
                                docsResponse.data()
                                        .stream()
                                        .map(doc -> doc.id())
                                        .toList();

                        var existingDocumentIds =
                                docRepository.findExistingIds(
                                        documentIds
                                );

                        for (var doc : docsResponse.data()) {
                            boolean newDocument =
                                    !existingDocumentIds.contains(
                                            doc.id()
                                    );

                            docRepository.upsert(doc);
                            docsImported++;

                            /*
                             * For a completely new affair its initial
                             * documents belong to AFFAIR_NEW and must not
                             * generate additional document notifications.
                             */
                            if (affairAlreadyExists && newDocument) {
                                newDocumentEvents.add(
                                        AlertEvent.newDocument(
                                                dto.id(),
                                                doc.id()
                                        )
                                );
                            }
                        }
                    }

                } catch (Exception exception) {
                    log.warn(
                            "Could not import documents for incremental affairId={}. " +
                                    "Import will continue. Reason: {}",
                            dto.id(),
                            exception.getMessage()
                    );
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

        return new ImportOrchestratorService.ImportResult(
                affairsImported,
                docsImported,
                importedAffairIds,
                newAffairIds,
                newDocumentEvents,
                maxUpdatedAt
        );
    }

    @Transactional
    public Long importAffairWithDocsById(
            Long affairId
    ) {
        var dto =
                openParlDataClient.fetchAffairById(
                        affairId
                );

        if (dto == null) {
            return null;
        }

        rawRepository.upsert(
                dto.id(),
                dto
        );

        affairStorePort.upsert(
                mapper.toDomain(dto)
        );

        try {
            var docsResponse =
                    openParlDataClient.fetchDocsForAffair(
                            dto.id()
                    );

            if (docsResponse != null
                    && docsResponse.data() != null) {

                for (var doc : docsResponse.data()) {
                    docRepository.upsert(doc);
                }
            }

        } catch (Exception exception) {
            log.warn(
                    "Could not import documents for affairId={}. Reason: {}",
                    dto.id(),
                    exception.getMessage()
            );
        }

        return dto.id();
    }
}