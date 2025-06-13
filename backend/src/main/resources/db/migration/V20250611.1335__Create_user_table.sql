CREATE TABLE IF NOT EXISTS users_
(
    id_       UUID PRIMARY KEY    NOT NULL DEFAULT gen_random_uuid(),
    username_ VARCHAR(255) UNIQUE NOT NULL,
    password_ VARCHAR(255)        NOT NULL
);