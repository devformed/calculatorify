CREATE TABLE IF NOT EXISTS history_
(
    id_            UUID PRIMARY KEY            NOT NULL DEFAULT gen_random_uuid(),
    user_id_       UUID                        NOT NULL CONSTRAINT user__fk REFERENCES user_ (id_) ON DELETE CASCADE,
    calculator_id_ UUID                        NOT NULL UNIQUE CONSTRAINT calculator__fk REFERENCES calculator_ (id_) ON DELETE CASCADE,
    accessed_at_   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);