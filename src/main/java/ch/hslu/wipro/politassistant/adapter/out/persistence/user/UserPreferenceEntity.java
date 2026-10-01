package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
public class UserPreferenceEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected UserPreferenceEntity() {
    }

    public UUID getId() {
        return id;
    }

    public AppUserEntity getUser() {
        return user;
    }

    public String getTopic() {
        return topic;
    }

    public String getChannel() {
        return channel;
    }

    public boolean isActive() {
        return active;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public static UserPreferenceEntity createPendingEmailSubscription(
            AppUserEntity user,
            String topic
    ) {
        UserPreferenceEntity preference =
                new UserPreferenceEntity();

        preference.id = UUID.randomUUID();
        preference.user = user;
        preference.topic = topic;
        preference.channel = "EMAIL";
        preference.active = false;
        preference.createdAt = LocalDateTime.now();

        return preference;
    }

    public static UserPreferenceEntity createEmailSubscription(
            AppUserEntity user,
            String topic
    ) {
        UserPreferenceEntity preference =
                createPendingEmailSubscription(user, topic);

        preference.active = true;

        return preference;
    }
}