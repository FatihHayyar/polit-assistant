package ch.hslu.wipro.politassistant.adapter.out.persistence.alert;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertJpaRepository extends JpaRepository<AlertEntity, UUID> {

    boolean existsByEventKeyAndTopicAndChannelAndRecipientEmail(
            String eventKey,
            String topic,
            String channel,
            String recipientEmail
    );

    long countByStatus(String status);

    List<AlertEntity> findByStatus(String status);

    List<AlertEntity> findByStatusInAndRetryCountLessThan(
            List<String> statuses,
            int retryCount
    );
}