CREATE TABLE IF NOT EXISTS calculator_
(
    id_          UUID PRIMARY KEY            NOT NULL DEFAULT gen_random_uuid(),
    title_       VARCHAR(255)                NOT NULL,
    description_ TEXT,
    config_      JSONB                        NOT NULL,
    created_at_  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at_  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    user_id_     UUID                        NOT NULL CONSTRAINT user__fl REFERENCES user_ (id_) ON DELETE CASCADE
);