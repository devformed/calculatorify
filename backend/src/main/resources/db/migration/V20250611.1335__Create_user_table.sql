CREATE TABLE IF NOT EXISTS user_
(
    id_       UUID PRIMARY KEY    NOT NULL DEFAULT gen_random_uuid(),
    username_ VARCHAR(255) UNIQUE NOT NULL,
    password_ VARCHAR(255)        NOT NULL
);

INSERT INTO user_ (id_, username_, password_) VALUES ('00000000-0000-0000-0000-000000000000', 'admin', '$2a$12$b9eJGYp/RL/YZDsxFnEFgeDc2cFGmEjBlxq5vv0JZ8TjL8899k5te')