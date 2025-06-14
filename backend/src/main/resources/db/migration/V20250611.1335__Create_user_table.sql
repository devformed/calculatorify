CREATE TABLE IF NOT EXISTS user_
(
    id_       UUID PRIMARY KEY    NOT NULL DEFAULT gen_random_uuid(),
    username_ VARCHAR(255) UNIQUE NOT NULL,
    password_ VARCHAR(255)        NOT NULL,
    roles_    TEXT                NOT NULL DEFAULT ',USER,'
);

INSERT INTO user_ (id_, username_, roles_, password_)
VALUES ('00000000-0000-0000-0000-000000000000', 'admin', ',ADMIN,', '$2a$12$b9eJGYp/RL/YZDsxFnEFgeDc2cFGmEjBlxq5vv0JZ8TjL8899k5te')