package ch.hslu.wipro.politassistant.adapter.out.persistence.alert;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class AlertEntity {

    @Id
    private UUID id;

    @Column(name = "affair_id", nullable = false)
    private Long affairId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_error")
    private String lastError;

    @Column(name= "recipient_email")
    private String recipientEmail;

    protected AlertEntity() {}

    public static AlertEntity pending(
            Long affairId,
            String topic,
            String channel,
            String recipientEmail,
            String title,
            String message
    ) {
        AlertEntity alert = new AlertEntity();
        alert.id = UUID.randomUUID();
        alert.affairId = affairId;
        alert.topic = topic;
        alert.channel = channel;
        alert.recipientEmail = recipientEmail;
        alert.status = "PENDING";
        alert.title = title;
        alert.message = message;
        alert.createdAt = LocalDateTime.now();
        alert.retryCount = 0;
        return alert;
    }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getChannel() { return channel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getRecipientEmail() { return recipientEmail; }

    public void markSent() {
        this.status = "SENT";
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }
    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.retryCount++;
        this.lastError = errorMessage;
    }

    public UUID getId() { return id; }
    public Long getAffairId() { return affairId; }
    public String getTopic() { return topic; }
    public String getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public String getLastError() { return lastError; }
}