package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SyncStateService {

    public static final String OPENPARLDATA_AFFAIRS = "OPENPARLDATA_AFFAIRS";
    public static final String OPENPARLDATA_MEETINGS = "OPENPARLDATA_MEETINGS";
    private final SyncStateJpaRepository repository;

    public SyncStateService(SyncStateJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LocalDateTime getLastSuccessfulSync(String source) {
        return repository.findById(source)
                .map(SyncStateEntity::getLastSuccessfulSync)
                .orElse(null);
    }

    @Transactional
    public void updateLastSuccessfulSync(
            String source,
            LocalDateTime syncTime
    ) {
        SyncStateEntity state = repository.findById(source)
                .orElseGet(() -> new SyncStateEntity(source));

        state.updateLastSuccessfulSync(syncTime);
        repository.save(state);
    }
}