package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPreferenceJpaRepository
        extends JpaRepository<UserPreferenceEntity, UUID> {

    List<UserPreferenceEntity> findByTopicAndActiveTrue(String topic);
}