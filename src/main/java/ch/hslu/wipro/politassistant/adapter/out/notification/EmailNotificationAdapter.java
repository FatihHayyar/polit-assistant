package ch.hslu.wipro.politassistant.adapter.out.notification;

import ch.hslu.wipro.politassistant.application.port.out.NotificationSender;
import ch.hslu.wipro.politassistant.domain.notification.Notification;
import ch.hslu.wipro.politassistant.domain.notification.NotificationChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationAdapter implements NotificationSender {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailNotificationAdapter(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(notification.recipientEmail());
        message.setSubject(notification.title());
        message.setText(notification.message());

        mailSender.send(message);
    }
}