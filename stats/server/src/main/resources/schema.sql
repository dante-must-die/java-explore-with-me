CREATE TABLE IF NOT EXISTS users (
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(254) NOT NULL,
    name     VARCHAR(250) NOT NULL,
    CONSTRAINT uq_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id                BIGSERIAL PRIMARY KEY,
    annotation        VARCHAR(2000) NOT NULL,
    description       VARCHAR(7000) NOT NULL,
    title             VARCHAR(120)  NOT NULL,
    created_on        TIMESTAMP     NOT NULL,
    published_on      TIMESTAMP,
    event_date        TIMESTAMP     NOT NULL,
    category_id       BIGINT        NOT NULL,
    initiator_id      BIGINT        NOT NULL,
    paid              BOOLEAN       DEFAULT FALSE,
    participant_limit INT           DEFAULT 0,
    request_moderation BOOLEAN      DEFAULT TRUE,
    state             VARCHAR(50)   NOT NULL,
    views             BIGINT        DEFAULT 0,
    CONSTRAINT fk_category
        FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_initiator
        FOREIGN KEY (initiator_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id      BIGSERIAL PRIMARY KEY,
    pinned  BOOLEAN   NOT NULL DEFAULT FALSE,
    title   VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilation
        FOREIGN KEY (compilation_id) REFERENCES compilations (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_compilation_event
        FOREIGN KEY (event_id) REFERENCES events (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGSERIAL PRIMARY KEY,
    created      TIMESTAMP NOT NULL,
    event_id     BIGINT    NOT NULL,
    requester_id BIGINT    NOT NULL,
    status       VARCHAR(50) NOT NULL,
    CONSTRAINT uq_request UNIQUE (event_id, requester_id),
    CONSTRAINT fk_event
        FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requester
        FOREIGN KEY (requester_id) REFERENCES users (id)
);
