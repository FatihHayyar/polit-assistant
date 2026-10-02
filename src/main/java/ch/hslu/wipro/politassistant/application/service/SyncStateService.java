package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SyncStateService {

    public static final String OPENPARLDATA_AFFAIRS =
            "OPENPARLDATA_AFFAIRS";

    public static final String OPENPARLDATA_MEETINGS =
            "OPENPARLDATA_MEETINGS";

    public static final String OPENPARLDATA_INITIAL_AFFAIRS =
            "OPENPARLDATA_INITIAL_AFFAIRS";

    public static final String OPENPARLDATA_INITIAL_DOCS =
            "OPENPARLDATA_INITIAL_DOCS";

    private final SyncStateJpaRepository repository;

    public SyncStateService(
            SyncStateJpaRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LocalDateTime getLastSuccessfulSync(
            String source
    ) {
        return repository.findById(source)
                .map(SyncStateEntity::getLastSuccessfulSync)
                .orElse(null);
    }

    @Transactional
    public void updateLastSuccessfulSync(
            String source,
            LocalDateTime syncTime
    ) {
        SyncStateEntity state =
                repository.findById(source)
                        .orElseGet(() -> new SyncStateEntity(source));

        state.updateLastSuccessfulSync(syncTime);
        repository.save(state);
    }

    @Transactional(readOnly = true)
    public Integer getLastOffset(
            String source
    ) {
        return repository.findById(source)
                .map(SyncStateEntity::getLastOffset)
                .orElse(null);
    }

    @Transactional
    public void updateLastOffset(
            String source,
            int offset
    ) {
        SyncStateEntity state =
                repository.findById(source)
                        .orElseGet(() -> new SyncStateEntity(source));

        state.updateLastOffset(offset);
        repository.save(state);
    }

    @Transactional
    public void clearLastOffset(
            String source
    ) {
        repository.findById(source)
                .ifPresent(state -> {
                    state.updateLastOffset(null);
                    repository.save(state);
                });
    }
}