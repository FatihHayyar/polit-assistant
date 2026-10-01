package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.domain.classification.Topic;

public record ChatQuery(
        ChatIntent intent,
        Topic topic,
        String bodyKey,
        String locationDisplayName,
        String searchText,
        SortOrder sortOrder,
        int limit
) {

    public enum ChatIntent {
        COUNT,
        LIST,
        SUMMARY
    }

    public enum SortOrder {
        NEWEST,
        RELEVANCE
    }

    public ChatQuery {

        if (intent == null) {
            intent = ChatIntent.SUMMARY;
        }

        if (sortOrder == null) {
            sortOrder = SortOrder.NEWEST;
        }

        if (limit < 1) {
            limit = 5;
        }

        if (limit > 10) {
            limit = 10;
        }

        searchText = normalize(searchText);
        bodyKey = normalize(bodyKey);
        locationDisplayName = normalize(locationDisplayName);
    }

    private static String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}