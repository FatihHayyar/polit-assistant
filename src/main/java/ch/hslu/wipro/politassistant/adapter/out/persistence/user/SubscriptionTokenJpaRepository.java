package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionTokenJpaRepository
        extends JpaRepository<SubscriptionTokenEntity, UUID> {

    Optional<SubscriptionTokenEntity>
    findByTokenHashAndTokenType(
            String tokenHash,
            String tokenType
    );

    void deleteByUserId(UUID userId);
}