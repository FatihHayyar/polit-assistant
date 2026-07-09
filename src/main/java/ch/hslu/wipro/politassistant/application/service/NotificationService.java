package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertEntity;
import ch.hslu.wipro.politassistant.application.port.out.NotificationPort;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationPort notificationPort;

    public NotificationService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }

    public void notify(AlertEntity alert) {
        NotificationChannel channel = NotificationChannel.valueOf(alert.getChannel());

        notificationPort.send(new Notification(
                alert.getAffairId(),
                alert.getRecipientEmail(),
                channel,
                alert.getTopic(),
                alert.getTitle(),
                alert.getMessage()
        ));
    }
}