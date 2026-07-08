package ch.hslu.wipro.politassistant.domain.affair;

import java.time.LocalDateTime;

public record Affair(
        Long id,
        String bodyKey,
        Long bodyId,
        String externalId,
        String number,
        String titleDe,
        String titleLongDe,
        String typeNameDe,
        String typeHarmonizedDe,
        String stateNameDe,
        LocalDateTime beginDate,
        LocalDateTime endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String urlApi,
        String urlExternalDe
) {}