package ch.hslu.wipro.politassistant.domain.importjob;

import java.time.LocalDateTime;
import java.util.UUID;

public record ImportJob(
        UUID id,
        String jobType,
        ImportJobStatus status,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        int recordsImported,
        int recordsUpdated,
        int recordsFailed,
        String errorMessage
) {
    public static ImportJob started(String jobType) {
        return new ImportJob(
                UUID.randomUUID(),
                jobType,
                ImportJobStatus.RUNNING,
                LocalDateTime.now(),
                null,
                0,
                0,
                0,
                null
        );
    }
}