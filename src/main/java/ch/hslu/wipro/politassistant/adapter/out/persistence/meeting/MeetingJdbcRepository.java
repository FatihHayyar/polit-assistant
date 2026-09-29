package ch.hslu.wipro.politassistant.adapter.out.persistence.meeting;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse.MeetingDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public MeetingJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(MeetingDto meeting) {
        String sql = """
                INSERT INTO meetings (
                    id,
                    body_id,
                    body_key,
                    external_id,
                    name_de,
                    begin_date,
                    end_date,
                    state,
                    location,
                    url_external_de,
                    updated_at,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?,
                    CAST(? AS TIMESTAMP),
                    CAST(? AS TIMESTAMP),
                    ?, ?, ?,
                    CAST(? AS TIMESTAMP),
                    CAST(? AS TIMESTAMP)
                )
                ON CONFLICT (id) DO UPDATE SET
                    body_id = EXCLUDED.body_id,
                    body_key = EXCLUDED.body_key,
                    external_id = EXCLUDED.external_id,
                    name_de = EXCLUDED.name_de,
                    begin_date = EXCLUDED.begin_date,
                    end_date = EXCLUDED.end_date,
                    state = EXCLUDED.state,
                    location = EXCLUDED.location,
                    url_external_de = EXCLUDED.url_external_de,
                    updated_at = EXCLUDED.updated_at
                """;

        jdbcTemplate.update(
                sql,
                meeting.id(),
                meeting.body_id(),
                meeting.body_key(),
                meeting.external_id(),
                getGermanValue(meeting.name()),
                meeting.begin_date(),
                meeting.end_date(),
                meeting.state(),
                meeting.location(),
                getGermanValue(meeting.url_external()),
                meeting.updated_at(),
                meeting.created_at()
        );
    }

    private String getGermanValue(java.util.Map<String, String> values) {
        if (values == null) {
            return null;
        }
        return values.get("de");
    }
}