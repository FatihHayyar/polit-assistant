package ch.hslu.wipro.politassistant.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BulkDocImportOrchestratorService {

    private static final Logger log =
            LoggerFactory.getLogger(BulkDocImportOrchestratorService.class);

    private static final String DOC_EXPORT_BASE_URL =
            "https://files.openparldata.ch/exports/docs/";

    private static final Path DATA_DIRECTORY =
            Path.of("data", "openparldata", "docs");

    private static final String CHECKPOINT_PREFIX =
            "OPENPARLDATA_BULK_DOCS_";

    private final JdbcTemplate jdbcTemplate;
    private final BulkDocImportService bulkDocImportService;
    private final SyncStateService syncStateService;
    private final HttpClient httpClient;

    public BulkDocImportOrchestratorService(
            JdbcTemplate jdbcTemplate,
            BulkDocImportService bulkDocImportService,
            SyncStateService syncStateService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.bulkDocImportService = bulkDocImportService;
        this.syncStateService = syncStateService;

        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(30))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();
    }

    public BulkDocsImportResult importAllRequiredDocShards() {

        ensureDataDirectory();

        List<String> bodyKeys =
                loadRequiredBodyKeys();

        log.info(
                "Starting bulk docs initial load for {} body keys",
                bodyKeys.size()
        );

        int totalShards = bodyKeys.size();
        int completedShards = 0;
        int skippedCompletedShards = 0;
        long totalProcessed = 0;
        long totalImported = 0;
        long totalSkippedNoAffair = 0;
        long totalFailed = 0;

        List<String> failedShards =
                new ArrayList<>();

        for (int index = 0; index < bodyKeys.size(); index++) {

            String bodyKey =
                    bodyKeys.get(index);

            String checkpoint =
                    CHECKPOINT_PREFIX + bodyKey;

            String filename =
                    "docs_" + bodyKey + ".ndjson.gz";

            Path localFile =
                    DATA_DIRECTORY.resolve(filename);

            int shardNumber =
                    index + 1;

            if (syncStateService.getLastSuccessfulSync(checkpoint) != null) {

                skippedCompletedShards++;

                log.info(
                        "Skipping completed doc shard {}/{}: {}",
                        shardNumber,
                        totalShards,
                        filename
                );

                continue;
            }

            log.info(
                    "Processing doc shard {}/{}: {}",
                    shardNumber,
                    totalShards,
                    filename
            );

            try {

                downloadIfNecessary(
                        filename,
                        localFile
                );

                BulkDocImportService.BulkDocImportResult result =
                        bulkDocImportService.importFile(
                                localFile
                        );

                totalProcessed +=
                        result.processed();

                totalImported +=
                        result.imported();

                totalSkippedNoAffair +=
                        result.skippedNoAffair();

                totalFailed +=
                        result.failed();

                if (result.failed() > 0) {

                    failedShards.add(filename);

                    log.error(
                            "Shard {} contained {} failed records. " +
                                    "Checkpoint will NOT be written.",
                            filename,
                            result.failed()
                    );

                    /*
                     * Stop here deliberately.
                     *
                     * Continuing would make it harder to notice that one
                     * shard was incomplete. A later run resumes from this
                     * shard because no checkpoint has been written.
                     */
                    break;
                }

                syncStateService.updateLastSuccessfulSync(
                        checkpoint,
                        LocalDateTime.now()
                );

                completedShards++;

                deleteLocalFile(
                        localFile
                );

                log.info(
                        "Completed doc shard {}/{}: {}",
                        shardNumber,
                        totalShards,
                        filename
                );

            } catch (Exception exception) {

                failedShards.add(filename);

                log.error(
                        "Bulk doc shard failed: {}. Reason: {}",
                        filename,
                        exception.getMessage(),
                        exception
                );

                /*
                 * Do not checkpoint and do not delete the downloaded file.
                 * The next run can reuse it.
                 */
                break;
            }
        }

        boolean complete =
                failedShards.isEmpty()
                        && completedShards
                        + skippedCompletedShards
                        == totalShards;

        log.info(
                "Bulk docs initial load finished: totalShards={}, completedNow={}, skippedCompleted={}, processed={}, imported={}, noAffair={}, failed={}, complete={}",
                totalShards,
                completedShards,
                skippedCompletedShards,
                totalProcessed,
                totalImported,
                totalSkippedNoAffair,
                totalFailed,
                complete
        );

        return new BulkDocsImportResult(
                totalShards,
                completedShards,
                skippedCompletedShards,
                totalProcessed,
                totalImported,
                totalSkippedNoAffair,
                totalFailed,
                complete,
                failedShards
        );
    }

    private List<String> loadRequiredBodyKeys() {

        return jdbcTemplate.queryForList(
                """
                SELECT DISTINCT body_key
                FROM affairs
                WHERE body_key IS NOT NULL
                  AND TRIM(body_key) <> ''
                ORDER BY body_key
                """,
                String.class
        );
    }

    private void downloadIfNecessary(
            String filename,
            Path target
    ) {

        try {

            if (Files.exists(target)) {

                long existingSize =
                        Files.size(target);

                if (existingSize > 0) {

                    log.info(
                            "Using existing downloaded shard: {} ({} bytes)",
                            target,
                            existingSize
                    );

                    return;
                }

                Files.delete(target);
            }

            URI uri =
                    URI.create(
                            DOC_EXPORT_BASE_URL + filename
                    );

            Path temporaryFile =
                    target.resolveSibling(
                            target.getFileName() + ".part"
                    );

            Files.deleteIfExists(
                    temporaryFile
            );

            log.info(
                    "Downloading {}",
                    uri
            );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uri)
                            .timeout(Duration.ofHours(2))
                            .GET()
                            .build();

            HttpResponse<InputStream> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofInputStream()
                    );

            if (response.statusCode() != 200) {

                try (InputStream ignored = response.body()) {
                    // Close response body.
                }

                throw new IllegalStateException(
                        "Download failed for "
                                + filename
                                + " with HTTP "
                                + response.statusCode()
                );
            }

            try (InputStream inputStream = response.body()) {

                Files.copy(
                        inputStream,
                        temporaryFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            long downloadedSize =
                    Files.size(temporaryFile);

            if (downloadedSize <= 0) {

                Files.deleteIfExists(
                        temporaryFile
                );

                throw new IllegalStateException(
                        "Downloaded file is empty: "
                                + filename
                );
            }

            Files.move(
                    temporaryFile,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            log.info(
                    "Downloaded {} ({} bytes)",
                    filename,
                    downloadedSize
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Download interrupted for "
                            + filename,
                    exception
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not download "
                            + filename,
                    exception
            );
        }
    }

    private void deleteLocalFile(
            Path file
    ) {

        try {

            Files.deleteIfExists(
                    file
            );

            log.info(
                    "Deleted completed local shard {}",
                    file
            );

        } catch (IOException exception) {

            /*
             * Import is already complete and checkpointed.
             * Failure to clean up a local file must not invalidate
             * the successful import.
             */
            log.warn(
                    "Could not delete completed local shard {}. Reason: {}",
                    file,
                    exception.getMessage()
            );
        }
    }

    private void ensureDataDirectory() {

        try {

            Files.createDirectories(
                    DATA_DIRECTORY
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Could not create bulk docs directory: "
                            + DATA_DIRECTORY.toAbsolutePath(),
                    exception
            );
        }
    }

    public record BulkDocsImportResult(
            int totalShards,
            int completedShards,
            int skippedCompletedShards,
            long processed,
            long imported,
            long skippedNoAffair,
            long failed,
            boolean complete,
            List<String> failedShards
    ) {
    }
}