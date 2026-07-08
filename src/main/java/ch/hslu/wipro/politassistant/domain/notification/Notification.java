package ch.hslu.wipro.politassistant.domain.notification;

public record Notification(

        Long affairId,

        String topic,

        String title,

        String message

) {
}