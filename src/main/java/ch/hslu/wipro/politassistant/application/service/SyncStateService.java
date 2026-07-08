package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateEntity;
import ch.hslu.wipro.politassistant.adapter.out.persistence.sync.SyncStateJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SyncStateService {

    public static final String OPENPARLDATA = "OPENPARLDATA";

    private final SyncStateJpaRepository repository;

    public SyncStateService(SyncStateJpaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LocalDateTime getLastSuccessfulSync() {
        return repository.findById(OPENPARLDATA)
                .map(SyncStateEntity::getLastSuccessfulSync)
                .orElse(null);
    }

    @Transactional
    public void updateLastSuccessfulSync(LocalDateTime syncTime) {
        SyncStateEntity state = repository.findById(OPENPARLDATA)
                .orElseGet(() -> new SyncStateEntity(OPENPARLDATA));

        state.updateLastSuccessfulSync(syncTime);
        repository.save(state);
    }
}