package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class AppUserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AppUserEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVerified() {
        return verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void verify() {
        this.verified = true;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "Der Anzeigename darf nicht leer sein."
            );
        }

        this.displayName = displayName.trim();
    }

    public void updateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Die E-Mail-Adresse darf nicht leer sein."
            );
        }

        this.email = email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    public void updateStatus(
            boolean active,
            boolean verified
    ) {
        this.active = active;
        this.verified = verified;

        if (!verified) {
            this.active = false;
        }
    }

    public static AppUserEntity createPending(
            String email,
            String displayName
    ) {
        AppUserEntity user = new AppUserEntity();

        user.id = UUID.randomUUID();

        user.email = email
                .trim()
                .toLowerCase(Locale.ROOT);

        user.displayName =
                displayName == null || displayName.isBlank()
                        ? email.trim()
                        : displayName.trim();

        user.active = false;
        user.verified = false;
        user.createdAt = LocalDateTime.now();

        return user;
    }
}