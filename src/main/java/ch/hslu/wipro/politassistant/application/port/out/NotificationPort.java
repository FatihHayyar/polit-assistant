package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.notification.Notification;

public interface NotificationPort {

    void send(Notification notification);

}