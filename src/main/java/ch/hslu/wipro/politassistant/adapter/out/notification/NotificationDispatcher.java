package ch.hslu.wipro.politassistant.adapter.out.notification;

import ch.hslu.wipro.politassistant.application.port.out.NotificationPort;
import ch.hslu.wipro.politassistant.application.port.out.NotificationSender;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationDispatcher implements NotificationPort {

    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationDispatcher(List<NotificationSender> senderList) {
        this.senders = new EnumMap<>(NotificationChannel.class);

        for (NotificationSender sender : senderList) {
            this.senders.put(sender.channel(), sender);
        }
    }

    @Override
    public void send(Notification notification) {
        NotificationSender sender = senders.get(notification.channel());

        if (sender == null) {
            throw new IllegalArgumentException(
                    "No notification sender configured for channel: " + notification.channel()
            );
        }

        sender.send(notification);
    }
}