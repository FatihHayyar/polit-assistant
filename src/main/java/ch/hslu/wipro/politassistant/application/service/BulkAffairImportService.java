package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkAffairDto;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.mapper.OpenParlDataBulkAffairMapper;
import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import tools.jackson.databind.ObjectMapper;
@Service
public class BulkAffairImportService {

    private static final Logger log =
            LoggerFactory.getLogger(BulkAffairImportService.class);

    private final ObjectMapper objectMapper;
    private final AffairStorePort affairStorePort;
    private final OpenParlDataBulkAffairMapper mapper;

    public BulkAffairImportService(
            ObjectMapper objectMapper,
            AffairStorePort affairStorePort,
            OpenParlDataBulkAffairMapper mapper
    ) {
        this.objectMapper = objectMapper;
        this.affairStorePort = affairStorePort;
        this.mapper = mapper;
    }

    public BulkAffairImportResult importFile(Path gzipFile) {

        if (!Files.exists(gzipFile)) {
            throw new IllegalArgumentException(
                    "Bulk affair file does not exist: " + gzipFile.toAbsolutePath()
            );
        }

        long processed = 0;
        long imported = 0;
        long failed = 0;

        log.info(
                "Starting bulk affair import from {}",
                gzipFile.toAbsolutePath()
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
                    OpenParlDataBulkAffairDto dto =
                            objectMapper.readValue(
                                    line,
                                    OpenParlDataBulkAffairDto.class
                            );

                    if (dto.id() == null) {
                        failed++;

                        log.warn(
                                "Skipping bulk affair without id at line {}",
                                processed
                        );

                        continue;
                    }

                    affairStorePort.upsert(
                            mapper.toDomain(dto)
                    );

                    imported++;

                } catch (Exception exception) {
                    failed++;

                    log.warn(
                            "Could not import bulk affair at line {}. Reason: {}",
                            processed,
                            exception.getMessage()
                    );
                }

                if (processed % 10_000 == 0) {
                    log.info(
                            "Bulk affair import progress: processed={}, imported={}, failed={}",
                            processed,
                            imported,
                            failed
                    );
                }
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not read bulk affair file: "
                            + gzipFile.toAbsolutePath(),
                    exception
            );
        }

        log.info(
                "Bulk affair import completed: processed={}, imported={}, failed={}",
                processed,
                imported,
                failed
        );

        return new BulkAffairImportResult(
                processed,
                imported,
                failed
        );
    }

    public record BulkAffairImportResult(
            long processed,
            long imported,
            long failed
    ) {
    }
}