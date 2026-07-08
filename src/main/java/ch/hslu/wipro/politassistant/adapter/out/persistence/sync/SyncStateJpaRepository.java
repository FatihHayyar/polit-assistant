package ch.hslu.wipro.politassistant.adapter.out.persistence.sync;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncStateJpaRepository extends JpaRepository<SyncStateEntity, String> {
}