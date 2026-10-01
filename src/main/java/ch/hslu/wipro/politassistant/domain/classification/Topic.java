package ch.hslu.wipro.politassistant.domain.classification;

import java.util.Arrays;

public enum Topic {

    ENERGIE("Energie"),
    BIODIVERSITAET("Biodiversität"),
    WASSER("Wasser"),
    LANDWIRTSCHAFT("Landwirtschaft"),
    RAUMPLANUNG("Raumplanung"),
    KLIMA("Klima"),
    MOBILITAET("Mobilität"),
    ABFALL("Abfall"),
    SONSTIGES("Sonstiges");

    private final String displayName;

    Topic(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Topic fromDisplayName(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(topic -> topic.displayName.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unbekanntes Thema: " + value)
                );
    }
}