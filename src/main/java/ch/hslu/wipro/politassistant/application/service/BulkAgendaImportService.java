package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkAgendaDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

@Service
public class BulkAgendaImportService {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public BulkAgendaImportService(
            ObjectMapper objectMapper,
            JdbcTemplate jdbcTemplate
    ) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public BulkAgendaImportResult importFile(Path file) throws Exception {
        long processed = 0;
        long imported = 0;
        long skippedMissingMeeting = 0;
        long skippedMissingAffair = 0;
        long failed = 0;

        try (
                GZIPInputStream gzipInputStream =
                        new GZIPInputStream(Files.newInputStream(file));
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        gzipInputStream,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                processed++;

                try {
                    OpenParlDataBulkAgendaDto agenda =
                            objectMapper.readValue(
                                    line,
                                    OpenParlDataBulkAgendaDto.class
                            );

                    if (agenda.id() == null || agenda.meeting_id() == null) {
                        failed++;
                        continue;
                    }

                    if (!exists("meetings", agenda.meeting_id())) {
                        skippedMissingMeeting++;
                        continue;
                    }

                    Long affairId = agenda.item_affair_id();

                    if (affairId != null && !exists("affairs", affairId)) {
                        affairId = null;
                        skippedMissingAffair++;
                    }

                    upsert(agenda, affairId);
                    imported++;

                } catch (Exception e) {
                    failed++;
                }

                if (processed % 10_000 == 0) {
                    System.out.printf(
                            "Bulk agenda progress: processed=%d imported=%d missingMeeting=%d missingAffair=%d failed=%d%n",
                            processed,
                            imported,
                            skippedMissingMeeting,
                            skippedMissingAffair,
                            failed
                    );
                }
            }
        }

        return new BulkAgendaImportResult(
                file.getFileName().toString(),
                processed,
                imported,
                skippedMissingMeeting,
                skippedMissingAffair,
                failed
        );
    }

    private boolean exists(String table, Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE id = ?",
                Integer.class,
                id
        );

        return count != null && count > 0;
    }

    private void upsert(
            OpenParlDataBulkAgendaDto agenda,
            Long affairId
    ) {
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
                affairId,
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

    public record BulkAgendaImportResult(
            String file,
            long processed,
            long imported,
            long skippedMissingMeeting,
            long skippedMissingAffair,
            long failed
    ) {
    }
}