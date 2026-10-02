package ch.hslu.wipro.politassistant.application.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricalClassificationService {

    public static final String SYNC_KEY =
            "HISTORICAL_AFFAIR_CLASSIFICATION_V3";

    private static final int BATCH_SIZE = 500;
    private static final int PROGRESS_INTERVAL = 5000;

    private final JdbcTemplate jdbcTemplate;
    private final RuleBasedClassificationService classificationService;
    private final SyncStateService syncStateService;

    public HistoricalClassificationService(
            JdbcTemplate jdbcTemplate,
            RuleBasedClassificationService classificationService,
            SyncStateService syncStateService
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.classificationService = classificationService;
        this.syncStateService = syncStateService;
    }

    public HistoricalClassificationResult classifyAll() {

        long totalAffairs = countAffairs();

        Integer checkpoint =
                syncStateService.getLastOffset(SYNC_KEY);

        long lastProcessedId =
                checkpoint == null
                        ? 0L
                        : checkpoint.longValue();

        long processedThisRun = 0;
        long classifiedThisRun = 0;
        long failedThisRun = 0;

        System.out.printf(
                "Historical classification started. " +
                        "total=%d checkpoint=%d batchSize=%d%n",
                totalAffairs,
                lastProcessedId,
                BATCH_SIZE
        );

        while (true) {

            List<Long> affairIds =
                    loadNextAffairIds(
                            lastProcessedId
                    );

            if (affairIds.isEmpty()) {
                break;
            }

            long batchLastId =
                    affairIds.getLast();

            try {

                var results =
                        classificationService
                                .classifyBatch(
                                        affairIds
                                );

                processedThisRun +=
                        affairIds.size();

                classifiedThisRun +=
                        results.size();

                saveCheckpoint(batchLastId);

                lastProcessedId =
                        batchLastId;

            } catch (Exception exception) {

                failedThisRun +=
                        affairIds.size();

                System.err.printf(
                        "Historical classification batch failed. " +
                                "firstAffairId=%d " +
                                "lastAffairId=%d " +
                                "batchSize=%d error=%s%n",
                        affairIds.getFirst(),
                        batchLastId,
                        affairIds.size(),
                        exception.getMessage()
                );

                /*
                 * Do not advance the checkpoint.
                 * A database or batch error must not silently
                 * skip 500 historical affairs.
                 */
                throw new IllegalStateException(
                        "Historical classification stopped at batch "
                                + affairIds.getFirst()
                                + "-"
                                + batchLastId,
                        exception
                );
            }

            if (processedThisRun
                    % PROGRESS_INTERVAL == 0) {

                System.out.printf(
                        "Historical classification progress: " +
                                "processedThisRun=%d " +
                                "classified=%d " +
                                "failed=%d " +
                                "lastAffairId=%d%n",
                        processedThisRun,
                        classifiedThisRun,
                        failedThisRun,
                        lastProcessedId
                );
            }
        }

        syncStateService.updateLastSuccessfulSync(
                SYNC_KEY,
                LocalDateTime.now()
        );

        long totalClassifiedAffairs =
                countClassifiedAffairs();

        long relevantAffairs =
                countRelevantAffairs();

        System.out.printf(
                "Historical classification completed. " +
                        "processedThisRun=%d " +
                        "classifiedThisRun=%d " +
                        "failedThisRun=%d " +
                        "totalClassifiedAffairs=%d " +
                        "relevantAffairs=%d%n",
                processedThisRun,
                classifiedThisRun,
                failedThisRun,
                totalClassifiedAffairs,
                relevantAffairs
        );

        return new HistoricalClassificationResult(
                totalAffairs,
                processedThisRun,
                classifiedThisRun,
                failedThisRun,
                totalClassifiedAffairs,
                relevantAffairs,
                lastProcessedId,
                true
        );
    }

    public HistoricalClassificationStatus status() {

        Integer checkpoint =
                syncStateService.getLastOffset(
                        SYNC_KEY
                );

        long lastProcessedId =
                checkpoint == null
                        ? 0L
                        : checkpoint.longValue();

        LocalDateTime completedAt =
                syncStateService
                        .getLastSuccessfulSync(
                                SYNC_KEY
                        );

        return new HistoricalClassificationStatus(
                countAffairs(),
                countClassifiedAffairs(),
                countRelevantAffairs(),
                lastProcessedId,
                completedAt,
                completedAt != null
        );
    }

    public HistoricalClassificationStatus reset() {

        jdbcTemplate.update(
                """
                DELETE FROM sync_state
                WHERE source = ?
                """,
                SYNC_KEY
        );

        return status();
    }

    private List<Long> loadNextAffairIds(
            long lastProcessedId
    ) {

        return jdbcTemplate.queryForList(
                """
                SELECT id
                FROM affairs
                WHERE id > ?
                ORDER BY id
                LIMIT ?
                """,
                Long.class,
                lastProcessedId,
                BATCH_SIZE
        );
    }

    private long countAffairs() {

        Long count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM affairs
                        """,
                        Long.class
                );

        return count == null ? 0L : count;
    }

    private long countClassifiedAffairs() {

        Long count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(DISTINCT affair_id)
                        FROM affair_classifications
                        """,
                        Long.class
                );

        return count == null ? 0L : count;
    }

    private long countRelevantAffairs() {

        Long count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(DISTINCT affair_id)
                        FROM affair_classifications
                        WHERE topic <> 'SONSTIGES'
                        """,
                        Long.class
                );

        return count == null ? 0L : count;
    }

    private void saveCheckpoint(long affairId) {

        if (affairId > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Affair ID exceeds sync_state.last_offset " +
                            "integer capacity: "
                            + affairId
            );
        }

        syncStateService.updateLastOffset(
                SYNC_KEY,
                (int) affairId
        );
    }

    public record HistoricalClassificationResult(
            long totalAffairs,
            long processedThisRun,
            long classifiedThisRun,
            long failedThisRun,
            long totalClassifiedAffairs,
            long relevantAffairs,
            long lastProcessedAffairId,
            boolean complete
    ) {
    }

    public record HistoricalClassificationStatus(
            long totalAffairs,
            long classifiedAffairs,
            long relevantAffairs,
            long lastProcessedAffairId,
            LocalDateTime completedAt,
            boolean complete
    ) {
    }
}