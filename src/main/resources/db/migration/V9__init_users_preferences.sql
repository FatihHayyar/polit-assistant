CREATE TABLE app_users (
                           id UUID PRIMARY KEY,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           display_name VARCHAR(255) NOT NULL,
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_preferences (
                                  id UUID PRIMARY KEY,
                                  user_id UUID NOT NULL REFERENCES app_users(id),
                                  topic VARCHAR(50) NOT NULL,
                                  channel VARCHAR(30) NOT NULL DEFAULT 'TEAMS',
                                  active BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  UNIQUE (user_id, topic, channel)
);

ALTER TABLE alerts
    ADD COLUMN recipient_email VARCHAR(255);

CREATE INDEX idx_user_preferences_topic ON user_preferences(topic);
CREATE INDEX idx_alerts_recipient_email ON alerts(recipient_email);
INSERT INTO app_users (id, email, display_name)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'fatih@hslu.ch',
           'Fatih Hayyar'
       );

INSERT INTO user_preferences (id, user_id, topic, channel)
VALUES
    ('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111111', 'ENERGY', 'TEAMS'),
    ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'WATER', 'TEAMS'),
    ('22222222-2222-2222-2222-222222222223', '11111111-1111-1111-1111-111111111111', 'CLIMATE', 'TEAMS');