package ch.hslu.wipro.politassistant.domain.notification;



public record Notification(

        Long affairId,

        String recipientEmail,

        NotificationChannel channel,

        String topic,

        String title,

        String message

) {
}