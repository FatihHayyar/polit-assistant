package ch.hslu.wipro.politassistant.adapter.out.persistence.agenda;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAgendasResponse.AgendaDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AgendaJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgendaJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsert(AgendaDto agenda) {
        String sql = """
                INSERT INTO agendas (
                    id,
                    meeting_id,
                    affair_id,
                    body_id,
                    body_key,
                    item_date,
                    item_external_id,
                    item_title,
                    item_number_display,
                    item_number,
                    item_description,
                    item_status,
                    item_result,
                    item_category,
                    item_url,
                    item_affair_number,
                    item_language,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?,
                    CAST(? AS TIMESTAMP),
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    CAST(? AS TIMESTAMP)
                )
                ON CONFLICT (id) DO UPDATE SET
                    meeting_id = EXCLUDED.meeting_id,
                    affair_id = EXCLUDED.affair_id,
                    body_id = EXCLUDED.body_id,
                    body_key = EXCLUDED.body_key,
                    item_date = EXCLUDED.item_date,
                    item_external_id = EXCLUDED.item_external_id,
                    item_title = EXCLUDED.item_title,
                    item_number_display = EXCLUDED.item_number_display,
                    item_number = EXCLUDED.item_number,
                    item_description = EXCLUDED.item_description,
                    item_status = EXCLUDED.item_status,
                    item_result = EXCLUDED.item_result,
                    item_category = EXCLUDED.item_category,
                    item_url = EXCLUDED.item_url,
                    item_affair_number = EXCLUDED.item_affair_number,
                    item_language = EXCLUDED.item_language
                """;

        jdbcTemplate.update(
                sql,
                agenda.id(),
                agenda.meeting_id(),
                agenda.item_affair_id(),
                agenda.body_id(),
                agenda.body_key(),
                agenda.item_date(),
                agenda.item_external_id(),
                agenda.item_title(),
                agenda.item_number_display(),
                agenda.item_number(),
                agenda.item_description(),
                agenda.item_status(),
                agenda.item_result(),
                agenda.item_category(),
                agenda.item_url(),
                agenda.item_affair_number(),
                agenda.item_language(),
                agenda.created_at()
        );
    }
}