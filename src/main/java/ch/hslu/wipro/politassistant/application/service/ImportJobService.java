package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.importjob.ImportJobEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.importjob.ImportJobJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ImportJobService {

    private final ImportJobJpaRepository repository;

    public ImportJobService(ImportJobJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UUID start(String jobType) {
        return repository.save(ImportJobEntity.start(jobType)).getId();
    }

    @Transactional
    public void success(UUID jobId, int imported, int updated, int failed) {
        ImportJobEntity job = repository.findById(jobId).orElseThrow();
        job.markSuccess(imported, updated, failed);
    }

    @Transactional
    public void failed(UUID jobId, String errorMessage) {
        ImportJobEntity job = repository.findById(jobId).orElseThrow();
        job.markFailed(errorMessage);
    }
}