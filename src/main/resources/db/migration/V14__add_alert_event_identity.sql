ALTER TABLE alerts
    ADD COLUMN event_type VARCHAR(50),
    ADD COLUMN event_key VARCHAR(500);

UPDATE alerts
SET event_type = 'AFFAIR_NEW',
    event_key = 'LEGACY:' || id::text
WHERE event_type IS NULL
   OR event_key IS NULL;

ALTER TABLE alerts
    ALTER COLUMN event_type SET NOT NULL,
    ALTER COLUMN event_key SET NOT NULL;

CREATE UNIQUE INDEX uq_alerts_event_recipient
    ON alerts(event_key, topic, channel, recipient_email);

CREATE INDEX idx_alerts_event_type
    ON alerts(event_type);