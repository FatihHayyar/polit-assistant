package ch.hslu.wipro.politassistant.adapter.out.persistence.importjob;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ImportJobJpaRepository extends JpaRepository<ImportJobEntity, UUID> {
}