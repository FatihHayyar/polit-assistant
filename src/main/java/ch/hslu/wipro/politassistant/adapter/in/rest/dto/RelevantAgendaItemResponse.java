package ch.hslu.wipro.politassistant.adapter.in.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RelevantAgendaItemResponse(
        Long agendaId,
        Long meetingId,
        String meetingName,
        LocalDateTime meetingBeginDate,
        LocalDateTime meetingEndDate,
        LocalDateTime itemDate,
        String itemNumber,
        String itemTitle,
        Long affairId,
        String affairTitle,
        String affairUrl,
        List<String> topics
) {
}