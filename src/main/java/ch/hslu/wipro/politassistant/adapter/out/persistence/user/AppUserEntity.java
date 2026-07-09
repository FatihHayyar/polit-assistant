package ch.hslu.wipro.politassistant.adapter.out.persistence.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AppUserEntity() {}

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public boolean isActive() { return active; }

    public static AppUserEntity create(String email, String displayName) {
        AppUserEntity user = new AppUserEntity();
        user.id = UUID.randomUUID();
        user.email = email;
        user.displayName = displayName;
        user.active = true;
        user.createdAt = LocalDateTime.now();
        return user;
    }
}