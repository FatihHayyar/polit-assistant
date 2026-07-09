package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;

public interface NotificationSender {

    NotificationChannel channel();

    void send(Notification notification);
}