package ch.hslu.wipro.politassistant.adapter.out.persistence.importjob;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "import_job")
public class ImportJobEntity {

    @Id
    private UUID id;

    @Column(name = "job_type", nullable = false)
    private String jobType;

    @Column(nullable = false)
    private String status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "records_imported")
    private int recordsImported;

    @Column(name = "records_updated")
    private int recordsUpdated;

    @Column(name = "records_failed")
    private int recordsFailed;

    @Column(name = "error_message")
    private String errorMessage;

    protected ImportJobEntity() {}

    public static ImportJobEntity start(String jobType) {
        ImportJobEntity job = new ImportJobEntity();
        job.id = UUID.randomUUID();
        job.jobType = jobType;
        job.status = "RUNNING";
        job.startedAt = LocalDateTime.now();
        return job;
    }

    public void markSuccess(int imported, int updated, int failed) {
        this.status = "SUCCESS";
        this.finishedAt = LocalDateTime.now();
        this.recordsImported = imported;
        this.recordsUpdated = updated;
        this.recordsFailed = failed;
    }

    public void markFailed(String message) {
        this.status = "FAILED";
        this.finishedAt = LocalDateTime.now();
        this.errorMessage = message;
    }

    public UUID getId() {
        return id;
    }
}