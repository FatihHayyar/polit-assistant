package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_tokens")
public class SubscriptionTokenEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "token_type", nullable = false, length = 30)
    private String tokenType;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected SubscriptionTokenEntity() {
    }

    public UUID getId() {
        return id;
    }

    public AppUserEntity getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsable() {
        return !isUsed() && !isExpired();
    }

    public void markUsed() {
        this.usedAt = LocalDateTime.now();
    }

    public static SubscriptionTokenEntity create(
            AppUserEntity user,
            String tokenHash,
            String tokenType,
            LocalDateTime expiresAt
    ) {
        SubscriptionTokenEntity token =
                new SubscriptionTokenEntity();

        token.id = UUID.randomUUID();
        token.user = user;
        token.tokenHash = tokenHash;
        token.tokenType = tokenType;
        token.expiresAt = expiresAt;
        token.createdAt = LocalDateTime.now();

        return token;
    }
}