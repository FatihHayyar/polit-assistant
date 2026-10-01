package ch.hslu.wipro.politassistant.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatRequest(

        @Schema(
                description = "Frage an den Polit-Assistant",
                example = "Zeige mir parlamentarische Geschäfte zum Thema Wasser"
        )
        String question

) {
}