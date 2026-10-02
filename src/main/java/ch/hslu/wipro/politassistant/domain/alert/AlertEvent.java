package ch.hslu.wipro.politassistant.domain.alert;

public record AlertEvent(
        AlertEventType type,
        String eventKey,
        Long affairId,
        String targetTopic
) {

    public static AlertEvent newAffair(
            Long affairId
    ) {
        return new AlertEvent(
                AlertEventType.AFFAIR_NEW,
                "AFFAIR_NEW:" + affairId,
                affairId,
                null
        );
    }

    public static AlertEvent newDocument(
            Long affairId,
            Long documentId
    ) {
        return new AlertEvent(
                AlertEventType.DOCUMENT_NEW,
                "DOCUMENT_NEW:" + affairId + ":" + documentId,
                affairId,
                null
        );
    }

    public static AlertEvent newTopic(
            Long affairId,
            String topic
    ) {
        return new AlertEvent(
                AlertEventType.TOPIC_NEW,
                "TOPIC_NEW:" + affairId + ":" + topic,
                affairId,
                topic
        );
    }

    public static AlertEvent newAgenda(
            Long affairId,
            Long agendaId
    ) {
        return new AlertEvent(
                AlertEventType.AGENDA_NEW,
                "AGENDA_NEW:" + agendaId,
                affairId,
                null
        );
    }

    public static AlertEvent agendaDateChanged(
            Long affairId,
            Long agendaId,
            String newDate
    ) {
        return new AlertEvent(
                AlertEventType.AGENDA_DATE_CHANGED,
                "AGENDA_DATE_CHANGED:" + agendaId + ":" + newDate,
                affairId,
                null
        );
    }
}