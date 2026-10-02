package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkDocDto;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairDocJdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@Service
public class BulkDocImportService {

    private static final Logger log =
            LoggerFactory.getLogger(BulkDocImportService.class);

    private static final int BATCH_SIZE = 500;

    private final ObjectMapper objectMapper;
    private final AffairDocJdbcRepository docRepository;
    private final JdbcTemplate jdbcTemplate;

    public BulkDocImportService(
            ObjectMapper objectMapper,
            AffairDocJdbcRepository docRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.objectMapper = objectMapper;
        this.docRepository = docRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public BulkDocImportResult importFile(
            Path gzipFile
    ) {

        if (!Files.exists(gzipFile)) {
            throw new IllegalArgumentException(
                    "Bulk doc file does not exist: "
                            + gzipFile.toAbsolutePath()
            );
        }

        long processed = 0;
        long imported = 0;
        long skippedNoAffair = 0;
        long skippedUnknownAffair = 0;
        long failed = 0;

        List<OpenParlDataBulkDocDto> batch =
                new ArrayList<>(BATCH_SIZE);

        log.info(
                "Starting bulk doc import from {} with batch size {}",
                gzipFile.toAbsolutePath(),
                BATCH_SIZE
        );

        try (
                InputStream fileInputStream =
                        Files.newInputStream(gzipFile);

                GZIPInputStream gzipInputStream =
                        new GZIPInputStream(fileInputStream);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        gzipInputStream,
                                        StandardCharsets.UTF_8
                                ),
                                1024 * 1024
                        )
        ) {

            String line;

            while ((line = reader.readLine()) != null) {

                processed++;

                if (line.isBlank()) {
                    continue;
                }

                try {
                    OpenParlDataBulkDocDto doc =
                            objectMapper.readValue(
                                    line,
                                    OpenParlDataBulkDocDto.class
                            );

                    if (doc.id() == null) {
                        failed++;
                        continue;
                    }

                    if (doc.affair_id() == null) {
                        skippedNoAffair++;
                        continue;
                    }

                    batch.add(doc);

                    if (batch.size() >= BATCH_SIZE) {

                        BatchResult result =
                                persistBatch(batch);

                        imported += result.imported();
                        skippedUnknownAffair +=
                                result.skippedUnknownAffair();

                        batch.clear();
                    }

                } catch (Exception exception) {

                    failed++;

                    log.warn(
                            "Could not process bulk doc at line {} from {}. Reason: {}",
                            processed,
                            gzipFile.getFileName(),
                            exception.getMessage()
                    );
                }

                if (processed % 10_000 == 0) {

                    log.info(
                            "Bulk doc progress: file={}, processed={}, imported={}, buffered={}, noAffair={}, unknownAffair={}, failed={}",
                            gzipFile.getFileName(),
                            processed,
                            imported,
                            batch.size(),
                            skippedNoAffair,
                            skippedUnknownAffair,
                            failed
                    );
                }
            }

            if (!batch.isEmpty()) {

                BatchResult result =
                        persistBatch(batch);

                imported += result.imported();
                skippedUnknownAffair +=
                        result.skippedUnknownAffair();

                batch.clear();
            }

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not read bulk doc file: "
                            + gzipFile.toAbsolutePath(),
                    exception
            );
        }

        log.info(
                "Bulk doc import completed: file={}, processed={}, imported={}, noAffair={}, unknownAffair={}, failed={}",
                gzipFile.getFileName(),
                processed,
                imported,
                skippedNoAffair,
                skippedUnknownAffair,
                failed
        );

        return new BulkDocImportResult(
                gzipFile.getFileName().toString(),
                processed,
                imported,
                skippedNoAffair,
                skippedUnknownAffair,
                failed
        );
    }

    private BatchResult persistBatch(
            List<OpenParlDataBulkDocDto> batch
    ) {

        Set<Long> requestedAffairIds =
                new HashSet<>();

        for (OpenParlDataBulkDocDto doc : batch) {

            if (doc.affair_id() != null) {
                requestedAffairIds.add(
                        doc.affair_id()
                );
            }
        }

        if (requestedAffairIds.isEmpty()) {
            return new BatchResult(
                    0,
                    batch.size()
            );
        }

        String placeholders =
                String.join(
                        ",",
                        requestedAffairIds.stream()
                                .map(id -> "?")
                                .toList()
                );

        String sql =
                "SELECT id FROM affairs WHERE id IN ("
                        + placeholders
                        + ")";

        Set<Long> existingAffairIds =
                new HashSet<>(
                        jdbcTemplate.queryForList(
                                sql,
                                Long.class,
                                requestedAffairIds.toArray()
                        )
                );

        List<OpenParlDataBulkDocDto> validDocs =
                new ArrayList<>(batch.size());

        long skippedUnknownAffair = 0;

        for (OpenParlDataBulkDocDto doc : batch) {

            if (existingAffairIds.contains(
                    doc.affair_id()
            )) {

                validDocs.add(doc);

            } else {

                skippedUnknownAffair++;
            }
        }

        if (!validDocs.isEmpty()) {
            docRepository.upsertBatch(
                    validDocs
            );
        }

        return new BatchResult(
                validDocs.size(),
                skippedUnknownAffair
        );
    }

    private record BatchResult(
            long imported,
            long skippedUnknownAffair
    ) {
    }

    public record BulkDocImportResult(
            String file,
            long processed,
            long imported,
            long skippedNoAffair,
            long skippedUnknownAffair,
            long failed
    ) {
    }
}