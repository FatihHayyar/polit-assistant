package ch.hslu.wipro.politassistant.adapter.out.notification;

import ch.hslu.wipro.politassistant.application.port.out.NotificationSender;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutlookNotificationAdapter implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(OutlookNotificationAdapter.class);

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.OUTLOOK;
    }

    @Override
    public void send(Notification notification) {
        log.info("""
                
                ========= OUTLOOK NOTIFICATION MOCK =========
                To: {}
                Subject: {}
                
                {}
                =============================================
                """,
                notification.recipientEmail(),
                notification.title(),
                notification.message()
        );
    }
}