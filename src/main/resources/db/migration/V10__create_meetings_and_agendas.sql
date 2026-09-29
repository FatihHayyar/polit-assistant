CREATE TABLE meetings (
                          id              BIGINT PRIMARY KEY,
                          body_id         BIGINT,
                          body_key        VARCHAR(50),
                          external_id     VARCHAR(255),
                          name_de         TEXT,
                          begin_date      TIMESTAMP,
                          end_date        TIMESTAMP,
                          state           VARCHAR(100),
                          location        TEXT,
                          url_external_de TEXT,
                          updated_at      TIMESTAMP,
                          created_at      TIMESTAMP
);

CREATE TABLE agendas (
                         id                  BIGINT PRIMARY KEY,
                         meeting_id          BIGINT NOT NULL,
                         affair_id           BIGINT,
                         body_id             BIGINT,
                         body_key            VARCHAR(50),
                         item_date           TIMESTAMP,
                         item_external_id    VARCHAR(255),
                         item_title          TEXT,
                         item_number_display VARCHAR(255),
                         item_number         VARCHAR(255),
                         item_description    TEXT,
                         item_status         VARCHAR(255),
                         item_result         TEXT,
                         item_category       VARCHAR(255),
                         item_url            TEXT,
                         item_affair_number  VARCHAR(255),
                         item_language       VARCHAR(20),
                         created_at          TIMESTAMP,

                         CONSTRAINT fk_agendas_meeting
                             FOREIGN KEY (meeting_id)
                                 REFERENCES meetings(id)
                                 ON DELETE CASCADE
);

CREATE INDEX idx_agendas_meeting_id
    ON agendas(meeting_id);

CREATE INDEX idx_agendas_affair_id
    ON agendas(affair_id);

CREATE INDEX idx_meetings_begin_date
    ON meetings(begin_date);

CREATE INDEX idx_agendas_item_date
    ON agendas(item_date);