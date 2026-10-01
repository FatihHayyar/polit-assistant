package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPreferenceJpaRepository
        extends JpaRepository<UserPreferenceEntity, UUID> {

    long countByActiveTrue();

    List<UserPreferenceEntity> findByTopicAndActiveTrue(
            String topic
    );

    List<UserPreferenceEntity> findByUserId(
            UUID userId
    );

    Optional<UserPreferenceEntity>
    findByUserEmailAndTopicAndChannel(
            String email,
            String topic,
            String channel
    );
}