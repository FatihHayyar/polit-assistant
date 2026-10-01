package ch.hslu.wipro.politassistant.adapter.out.persistence.agenda;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.RelevantAgendaItemResponse;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
public class RelevantAgendaJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public RelevantAgendaJdbcRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RelevantAgendaItemResponse> findRelevantAgendaItems(
            LocalDateTime from,
            LocalDateTime until,
            int limit,
            int offset
    ) {
        String sql = """
                SELECT
                    ag.id AS agenda_id,
                    m.id AS meeting_id,
                    m.name_de AS meeting_name,
                    m.begin_date AS meeting_begin_date,
                    m.end_date AS meeting_end_date,
                    ag.item_date,
                    ag.item_number_display,
                    ag.item_title,
                    a.id AS affair_id,
                    a.title_de AS affair_title,
                    a.url_external_de AS affair_url,
                    ARRAY_AGG(
                        DISTINCT c.topic
                        ORDER BY c.topic
                    ) AS topics
                FROM agendas ag
                JOIN meetings m
                    ON m.id = ag.meeting_id
                JOIN affairs a
                    ON a.id = ag.affair_id
                JOIN affair_classifications c
                    ON c.affair_id = a.id
                WHERE c.topic <> 'SONSTIGES'
                  AND COALESCE(
                      ag.item_date,
                      m.begin_date
                  ) >= ?
                  AND COALESCE(
                      ag.item_date,
                      m.begin_date
                  ) < ?
                GROUP BY
                    ag.id,
                    m.id,
                    m.name_de,
                    m.begin_date,
                    m.end_date,
                    ag.item_date,
                    ag.item_number_display,
                    ag.item_title,
                    a.id,
                    a.title_de,
                    a.url_external_de
                ORDER BY
                    COALESCE(
                        ag.item_date,
                        m.begin_date
                    ) ASC NULLS LAST,
                    ag.id ASC
                LIMIT ?
                OFFSET ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Array sqlArray =
                            rs.getArray("topics");

                    List<String> topics =
                            sqlArray == null
                                    ? List.of()
                                    : Arrays.stream(
                                            (String[]) sqlArray.getArray()
                                    )
                                    .map(this::toDisplayName)
                                    .toList();

                    return new RelevantAgendaItemResponse(
                            rs.getLong("agenda_id"),
                            rs.getLong("meeting_id"),
                            rs.getString("meeting_name"),

                            rs.getTimestamp(
                                    "meeting_begin_date"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "meeting_begin_date"
                            ).toLocalDateTime(),

                            rs.getTimestamp(
                                    "meeting_end_date"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "meeting_end_date"
                            ).toLocalDateTime(),

                            rs.getTimestamp(
                                    "item_date"
                            ) == null
                                    ? null
                                    : rs.getTimestamp(
                                    "item_date"
                            ).toLocalDateTime(),

                            rs.getString(
                                    "item_number_display"
                            ),

                            rs.getString(
                                    "item_title"
                            ),

                            rs.getLong(
                                    "affair_id"
                            ),

                            rs.getString(
                                    "affair_title"
                            ),

                            rs.getString(
                                    "affair_url"
                            ),

                            topics
                    );
                },
                from,
                until,
                limit,
                offset
        );
    }

    private String toDisplayName(
            String databaseTopic
    ) {
        try {
            return Topic
                    .valueOf(databaseTopic)
                    .getDisplayName();

        } catch (IllegalArgumentException e) {
            return databaseTopic;
        }
    }
}