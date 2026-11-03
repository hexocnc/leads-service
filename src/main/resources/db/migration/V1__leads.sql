CREATE TABLE leads
(
    id          BIGSERIAL PRIMARY KEY,
    domain      VARCHAR(32)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    email       VARCHAR(256),
    status      VARCHAR(20)  NOT NULL,
    description TEXT,
    comment     VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP
);