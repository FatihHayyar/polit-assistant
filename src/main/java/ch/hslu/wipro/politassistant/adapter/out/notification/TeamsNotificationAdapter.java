package ch.hslu.wipro.politassistant.adapter.out.notification;

import ch.hslu.wipro.politassistant.application.port.out.NotificationSender;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TeamsNotificationAdapter implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(TeamsNotificationAdapter.class);

    private final RestClient restClient;
    private final String webhookUrl;

    public TeamsNotificationAdapter(
            RestClient restClient,
            @Value("${notification.teams.webhook-url:}") String webhookUrl
    ) {
        this.restClient = restClient;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void send(Notification notification) {
        String text = """
        **%s**

        Recipient: %s
        Channel: %s
        Topic: %s
        Affair ID: %s

        %s
        """.formatted(
                notification.title(),
                notification.recipientEmail(),
                notification.channel(),
                notification.topic(),
                notification.affairId(),
                notification.message()
        );

        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("""
                    
                    ========= TEAMS NOTIFICATION MOCK =========
                    {}
                    ===========================================
                    """, text);
            return;
        }

        restClient.post()
                .uri(webhookUrl)
                .body(Map.of("text", text))
                .retrieve()
                .toBodilessEntity();
    }
    @Override
    public NotificationChannel channel() {
        return NotificationChannel.TEAMS;
    }
}