ALTER TABLE app_users
    ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE app_users
SET verified = TRUE
WHERE active = TRUE;


CREATE TABLE subscription_tokens
(
    id          UUID PRIMARY KEY,
    user_id     UUID         NOT NULL,
    token_hash  VARCHAR(64)  NOT NULL UNIQUE,
    token_type  VARCHAR(30)  NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    used_at     TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL,

    CONSTRAINT fk_subscription_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES app_users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_subscription_tokens_user
    ON subscription_tokens (user_id);

CREATE INDEX idx_subscription_tokens_hash
    ON subscription_tokens (token_hash);

CREATE INDEX idx_subscription_tokens_expiry
    ON subscription_tokens (expires_at);