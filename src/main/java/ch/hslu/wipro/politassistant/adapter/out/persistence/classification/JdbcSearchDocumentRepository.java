package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcSearchDocumentRepository implements SearchDocumentPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSearchDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AffairSearchDocument load(Long affairId) {

        Map<Long, AffairSearchDocument> result =
                loadBatch(List.of(affairId));

        AffairSearchDocument document = result.get(affairId);

        if (document == null) {
            throw new IllegalArgumentException(
                    "Parlamentarisches Geschäft nicht gefunden: " + affairId
            );
        }

        return document;
    }

    @Override
    public Map<Long, AffairSearchDocument> loadBatch(List<Long> affairIds) {

        if (affairIds == null || affairIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, MutableDocument> documents =
                new LinkedHashMap<>();

        jdbcTemplate.query(
                connection -> {

                    Array ids = connection.createArrayOf(
                            "bigint",
                            affairIds.toArray()
                    );

                    var statement = connection.prepareStatement(
                            """
                            SELECT
                                id,
                                title_de,
                                title_long_de
                            FROM affairs
                            WHERE id = ANY (?)
                            ORDER BY id
                            """
                    );

                    statement.setArray(1, ids);

                    return statement;
                },
                rs -> {

                    long id = rs.getLong("id");

                    documents.put(
                            id,
                            new MutableDocument(
                                    id,
                                    rs.getString("title_de"),
                                    rs.getString("title_long_de"),
                                    new ArrayList<>()
                            )
                    );
                }
        );

        jdbcTemplate.query(
                connection -> {

                    Array ids = connection.createArrayOf(
                            "bigint",
                            affairIds.toArray()
                    );

                    var statement = connection.prepareStatement(
                            """
                            SELECT
                                affair_id,
                                text_content
                            FROM affair_docs
                            WHERE affair_id = ANY (?)
                              AND text_content IS NOT NULL
                              AND text_content <> ''
                            ORDER BY affair_id, id
                            """
                    );

                    statement.setArray(1, ids);

                    return statement;
                },
                rs -> {

                    long affairId =
                            rs.getLong("affair_id");

                    MutableDocument document =
                            documents.get(affairId);

                    if (document != null) {
                        document.documentContents().add(
                                rs.getString("text_content")
                        );
                    }
                }
        );

        Map<Long, AffairSearchDocument> result =
                new LinkedHashMap<>();

        documents.forEach(
                (id, document) ->
                        result.put(
                                id,
                                new AffairSearchDocument(
                                        document.id(),
                                        document.title(),
                                        document.titleLong(),
                                        List.copyOf(
                                                document.documentContents()
                                        )
                                )
                        )
        );

        return result;
    }

    private record MutableDocument(
            Long id,
            String title,
            String titleLong,
            List<String> documentContents
    ) {
    }
}