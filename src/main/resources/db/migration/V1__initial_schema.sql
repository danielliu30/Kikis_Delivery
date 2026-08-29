CREATE TABLE customers (
    email    VARCHAR(320) PRIMARY KEY,
    password VARCHAR(512) NOT NULL,
    name     VARCHAR(255),
    member   VARCHAR(32),
    admin    VARCHAR(32),
    updated  TIMESTAMP
);

CREATE TABLE baked_goods (
    id              BIGSERIAL PRIMARY KEY,
    baked_item      VARCHAR(128) NOT NULL,
    item_variation  VARCHAR(64)  NOT NULL,
    expiration_time TIMESTAMP,
    size            VARCHAR(64),
    shape           VARCHAR(64),
    count           VARCHAR(64),
    flavor          VARCHAR(128),
    layers          VARCHAR(64),
    calories        VARCHAR(64),
    toppings        VARCHAR(255),
    fillings        VARCHAR(255),
    vegan           VARCHAR(32),
    gluten_free     VARCHAR(32),
    cost            VARCHAR(64),
    CONSTRAINT uq_baked_goods_item_variation UNIQUE (baked_item, item_variation)
);

CREATE INDEX idx_baked_goods_baked_item ON baked_goods (baked_item);

CREATE TABLE validation_tokens (
    token_id         VARCHAR(64)  PRIMARY KEY,
    email            VARCHAR(320) NOT NULL,
    pending_customer TEXT         NOT NULL,
    expiration       TIMESTAMP    NOT NULL
);

CREATE TABLE customer_orders (
    id             BIGSERIAL PRIMARY KEY,
    customer_email VARCHAR(320) NOT NULL REFERENCES customers (email) ON DELETE CASCADE,
    purchased_at   TIMESTAMP    NOT NULL,
    item           TEXT         NOT NULL
);

CREATE INDEX idx_customer_orders_email ON customer_orders (customer_email);

CREATE TABLE store_front (
    id               VARCHAR(64) PRIMARY KEY,
    total_money_made NUMERIC(14, 2) NOT NULL DEFAULT 0
);

INSERT INTO store_front (id, total_money_made) VALUES ('TotalRevenue', 0);
