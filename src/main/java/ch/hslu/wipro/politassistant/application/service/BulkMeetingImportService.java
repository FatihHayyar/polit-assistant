package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkMeetingDto;
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
public class BulkMeetingImportService {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public BulkMeetingImportService(
            ObjectMapper objectMapper,
            JdbcTemplate jdbcTemplate
    ) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    public BulkMeetingImportResult importFile(Path file) throws Exception {
        long processed = 0;
        long imported = 0;
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
                    OpenParlDataBulkMeetingDto meeting =
                            objectMapper.readValue(
                                    line,
                                    OpenParlDataBulkMeetingDto.class
                            );

                    if (meeting.id() == null) {
                        failed++;
                        continue;
                    }

                    upsert(meeting);
                    imported++;

                } catch (Exception e) {
                    failed++;
                }
            }
        }

        return new BulkMeetingImportResult(
                file.getFileName().toString(),
                processed,
                imported,
                failed
        );
    }

    private void upsert(OpenParlDataBulkMeetingDto meeting) {
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
                meeting.name_de(),
                meeting.begin_date(),
                meeting.end_date(),
                meeting.state(),
                meeting.location(),
                meeting.url_external_de(),
                meeting.updated_at(),
                meeting.created_at()
        );
    }

    public record BulkMeetingImportResult(
            String file,
            long processed,
            long imported,
            long failed
    ) {
    }
}