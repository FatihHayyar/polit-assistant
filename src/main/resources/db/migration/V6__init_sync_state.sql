CREATE TABLE sync_state (
                            source VARCHAR(100) PRIMARY KEY,
                            last_successful_sync TIMESTAMP
);

INSERT INTO sync_state (source, last_successful_sync)
VALUES ('OPENPARLDATA', NULL);