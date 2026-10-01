package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.application.service.ChatQuery;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AffairSearchJdbcRepository {

    private static final int MAX_SNIPPET_CHARACTERS = 1200;
    private static final int MAX_DOCUMENTS_PER_AFFAIR = 2;

    private final JdbcTemplate jdbcTemplate;

    public AffairSearchJdbcRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AffairSummaryResponse> fullTextSearch(
            String query,
            String topic,
            int limit,
            int offset
    ) {

        return searchForChat(
                topic,
                null,
                query,
                ChatQuery.SortOrder.RELEVANCE,
                limit,
                offset
        );
    }

    public List<AffairSummaryResponse> filterByTopic(
            String topic,
            int limit,
            int offset
    ) {

        return searchForChat(
                topic,
                null,
                null,
                ChatQuery.SortOrder.NEWEST,
                limit,
                offset
        );
    }

    public List<AffairSummaryResponse> searchForChat(
            String topic,
            String bodyKey,
            String searchText,
            ChatQuery.SortOrder sortOrder,
            int limit,
            int offset
    ) {

        boolean hasTopic =
                topic != null && !topic.isBlank();

        boolean hasBodyKey =
                bodyKey != null && !bodyKey.isBlank();

        boolean hasSearchText =
                searchText != null && !searchText.isBlank();

        StringBuilder sql = new StringBuilder("""
                SELECT
                    a.id,
                    a.title_de,
                    a.type_name_de,
                    a.state_name_de,
                    c.topic,
                    c.confidence,
                    a.begin_date,
                    a.url_external_de
                FROM affairs a
                """);

        if (hasTopic) {
            sql.append("""
                    LEFT JOIN LATERAL (
                        SELECT
                            ac.topic,
                            ac.confidence
                        FROM affair_classifications ac
                        WHERE ac.affair_id = a.id
                          AND UPPER(ac.topic) = UPPER(?)
                        ORDER BY
                            ac.confidence DESC,
                            ac.topic
                        LIMIT 1
                    ) c ON TRUE
                    """);
        } else {
            sql.append("""
                    LEFT JOIN LATERAL (
                        SELECT
                            ac.topic,
                            ac.confidence
                        FROM affair_classifications ac
                        WHERE ac.affair_id = a.id
                        ORDER BY
                            ac.confidence DESC,
                            ac.topic
                        LIMIT 1
                    ) c ON TRUE
                    """);
        }

        sql.append("""
                WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (hasTopic) {
            params.add(topic);

            sql.append("""
                     AND c.topic IS NOT NULL
                    """);
        }

        if (hasBodyKey) {
            sql.append("""
                     AND UPPER(a.body_key) = UPPER(?)
                    """);

            params.add(bodyKey);
        }

        if (hasSearchText) {
            sql.append("""
                     AND (
                         a.search_vector @@ websearch_to_tsquery(
                             'german',
                             ?
                         )
                         OR EXISTS (
                             SELECT 1
                             FROM affair_docs d
                             WHERE d.affair_id = a.id
                               AND d.search_vector @@ websearch_to_tsquery(
                                   'german',
                                   ?
                               )
                         )
                     )
                    """);

            params.add(searchText);
            params.add(searchText);
        }

        if (hasSearchText
                && sortOrder == ChatQuery.SortOrder.RELEVANCE) {

            sql.append("""
                    ORDER BY
                        ts_rank(
                            a.search_vector,
                            websearch_to_tsquery('german', ?)
                        ) DESC,
                        a.begin_date DESC NULLS LAST,
                        a.id DESC
                    """);

            params.add(searchText);

        } else {

            sql.append("""
                    ORDER BY
                        a.begin_date DESC NULLS LAST,
                        a.id DESC
                    """);
        }

        sql.append("""
                LIMIT ?
                OFFSET ?
                """);

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> mapRow(rs),
                params.toArray()
        );
    }

    public List<AffairSummaryResponse> searchForChat(
            String topic,
            String bodyKey,
            int limit,
            int offset
    ) {

        return searchForChat(
                topic,
                bodyKey,
                null,
                ChatQuery.SortOrder.NEWEST,
                limit,
                offset
        );
    }

    public long countByTopic(
            String topic
    ) {

        return countForChat(
                topic,
                null,
                null
        );
    }

    public long countForChat(
            String topic,
            String bodyKey
    ) {

        return countForChat(
                topic,
                bodyKey,
                null
        );
    }

    public long countForChat(
            String topic,
            String bodyKey,
            String searchText
    ) {

        boolean hasTopic =
                topic != null && !topic.isBlank();

        boolean hasBodyKey =
                bodyKey != null && !bodyKey.isBlank();

        boolean hasSearchText =
                searchText != null && !searchText.isBlank();

        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(DISTINCT a.id)
                FROM affairs a
                """);

        List<Object> params = new ArrayList<>();

        if (hasTopic) {
            sql.append("""
                    JOIN affair_classifications ac
                      ON ac.affair_id = a.id
                    """);
        }

        sql.append("""
                WHERE 1 = 1
                """);

        if (hasTopic) {
            sql.append("""
                     AND UPPER(ac.topic) = UPPER(?)
                    """);

            params.add(topic);
        }

        if (hasBodyKey) {
            sql.append("""
                     AND UPPER(a.body_key) = UPPER(?)
                    """);

            params.add(bodyKey);
        }

        if (hasSearchText) {
            sql.append("""
                     AND (
                         a.search_vector @@ websearch_to_tsquery(
                             'german',
                             ?
                         )
                         OR EXISTS (
                             SELECT 1
                             FROM affair_docs d
                             WHERE d.affair_id = a.id
                               AND d.search_vector @@ websearch_to_tsquery(
                                   'german',
                                   ?
                               )
                         )
                     )
                    """);

            params.add(searchText);
            params.add(searchText);
        }

        Long count = jdbcTemplate.queryForObject(
                sql.toString(),
                Long.class,
                params.toArray()
        );

        return count == null ? 0L : count;
    }

    public String loadRelevantDocumentContext(
            Long affairId,
            String searchText
    ) {

        if (searchText == null || searchText.isBlank()) {
            return loadDocumentPreview(affairId);
        }

        String sql = """
                SELECT
                    d.name,
                    ts_headline(
                        'german',
                        d.text_content,
                        websearch_to_tsquery('german', ?),
                        'MaxWords=80, MinWords=30, MaxFragments=2'
                    ) AS snippet
                FROM affair_docs d
                WHERE d.affair_id = ?
                  AND d.text_content IS NOT NULL
                  AND LENGTH(TRIM(d.text_content)) > 0
                  AND d.search_vector @@ websearch_to_tsquery(
                      'german',
                      ?
                  )
                ORDER BY d.id
                LIMIT ?
                """;

        List<DocumentSnippet> snippets =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new DocumentSnippet(
                                        rs.getString("name"),
                                        rs.getString("snippet")
                                ),
                        searchText,
                        affairId,
                        searchText,
                        MAX_DOCUMENTS_PER_AFFAIR
                );

        if (snippets.isEmpty()) {
            return loadDocumentPreview(affairId);
        }

        return formatSnippets(snippets);
    }

    public String loadDocumentContext(
            Long affairId
    ) {

        return loadDocumentPreview(affairId);
    }

    private String loadDocumentPreview(
            Long affairId
    ) {

        String sql = """
                SELECT
                    d.name,
                    d.text_content
                FROM affair_docs d
                WHERE d.affair_id = ?
                  AND d.text_content IS NOT NULL
                  AND LENGTH(TRIM(d.text_content)) > 0
                ORDER BY d.id
                LIMIT ?
                """;

        List<DocumentSnippet> documents =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new DocumentSnippet(
                                        rs.getString("name"),
                                        shorten(
                                                rs.getString(
                                                        "text_content"
                                                )
                                        )
                                ),
                        affairId,
                        MAX_DOCUMENTS_PER_AFFAIR
                );

        return formatSnippets(documents);
    }

    private String formatSnippets(
            List<DocumentSnippet> snippets
    ) {

        if (snippets.isEmpty()) {
            return "";
        }

        StringBuilder result =
                new StringBuilder();

        for (DocumentSnippet snippet : snippets) {

            if (snippet.text() == null
                    || snippet.text().isBlank()) {
                continue;
            }

            result.append("Dokument: ")
                    .append(
                            snippet.name() == null
                                    || snippet.name().isBlank()
                                    ? "Ohne Titel"
                                    : snippet.name()
                    )
                    .append('\n')
                    .append(snippet.text().trim())
                    .append("\n\n");
        }

        return result.toString().trim();
    }

    private String shorten(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "";
        }

        String normalized = text
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.length()
                <= MAX_SNIPPET_CHARACTERS) {
            return normalized;
        }

        return normalized.substring(
                0,
                MAX_SNIPPET_CHARACTERS
        ) + " …";
    }

    private AffairSummaryResponse mapRow(
            java.sql.ResultSet rs
    ) throws java.sql.SQLException {

        String databaseTopic =
                rs.getString("topic");

        return new AffairSummaryResponse(
                rs.getLong("id"),
                rs.getString("title_de"),
                rs.getString("type_name_de"),
                rs.getString("state_name_de"),
                toTopicDisplayName(databaseTopic),
                rs.getObject(
                        "confidence",
                        java.math.BigDecimal.class
                ),
                rs.getTimestamp("begin_date") == null
                        ? null
                        : rs.getTimestamp(
                        "begin_date"
                ).toLocalDateTime(),
                rs.getString("url_external_de")
        );
    }

    private String toTopicDisplayName(
            String databaseTopic
    ) {

        if (databaseTopic == null
                || databaseTopic.isBlank()) {
            return null;
        }

        try {
            return Topic.valueOf(databaseTopic)
                    .getDisplayName();
        } catch (IllegalArgumentException e) {
            return databaseTopic;
        }
    }

    private record DocumentSnippet(
            String name,
            String text
    ) {
    }
}