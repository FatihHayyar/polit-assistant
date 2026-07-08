package ch.hslu.wipro.politassistant.adapter.out.persistence.sync;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_state")
public class SyncStateEntity {

    @Id
    private String source;

    @Column(name = "last_successful_sync")
    private LocalDateTime lastSuccessfulSync;

    protected SyncStateEntity() {}

    public SyncStateEntity(String source) {
        this.source = source;
    }

    public void updateLastSuccessfulSync(LocalDateTime value) {
        this.lastSuccessfulSync = value;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getLastSuccessfulSync() {
        return lastSuccessfulSync;
    }
}