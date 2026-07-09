package ch.hslu.wipro.politassistant.application.service;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class ChatIntentParser {

    public Optional<String> detectTopic(String question) {
        if (question == null || question.isBlank()) {
            return Optional.empty();
        }

        String q = question.toLowerCase(Locale.ROOT);

        if (containsAny(q, "energy", "energie", "strom", "elektrizität")) {
            return Optional.of("ENERGY");
        }

        if (containsAny(q, "water", "wasser", "gewässer", "fluss", "see")) {
            return Optional.of("WATER");
        }

        if (containsAny(q, "mobility", "verkehr", "mobilität", "bahn", "öv")) {
            return Optional.of("MOBILITY");
        }

        if (containsAny(q, "climate", "klima", "co2", "emission")) {
            return Optional.of("CLIMATE");
        }

        if (containsAny(q, "biodiversity", "biodiversität", "artenvielfalt", "wald")) {
            return Optional.of("BIODIVERSITY");
        }

        if (containsAny(q, "agriculture", "landwirtschaft", "pestizid", "boden")) {
            return Optional.of("AGRICULTURE");
        }

        if (containsAny(q, "spatial", "raumplanung", "bauzone", "siedlung")) {
            return Optional.of("SPATIAL_PLANNING");
        }

        if (containsAny(q, "waste", "abfall", "recycling", "plastik")) {
            return Optional.of("WASTE");
        }

        return Optional.empty();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}