CREATE TABLE customer
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(255)        NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE address
(
    id          BIGSERIAL PRIMARY KEY,
    street      VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20)  NOT NULL,
    country     VARCHAR(100),
    customer_id BIGINT REFERENCES customer (id)
);

CREATE TABLE product
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(255)   NOT NULL,
    description      TEXT,
    price            NUMERIC(19, 2) NOT NULL,
    status           VARCHAR(50)    NOT NULL, -- enum: IN_STOCK, OUT_OF_STOCK, DISCONTINUED
    quantity_on_hand INTEGER        NOT NULL
);

CREATE TABLE orders
(
    id               BIGSERIAL PRIMARY KEY,
    creation_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price      NUMERIC(19, 2),
    shipping_charge  NUMERIC(19, 2),
    is_shipped       BOOLEAN   DEFAULT FALSE,
    customer_id      BIGINT REFERENCES customer (id),
    shipping_street  VARCHAR(255) NOT NULL,
    shipping_city    VARCHAR(100) NOT NULL,
    shipping_postal_code VARCHAR(20) NOT NULL,
    shipping_country VARCHAR(100) NOT NULL
);

CREATE TABLE order_item
(
    id         BIGSERIAL PRIMARY KEY,
    quantity   INTEGER NOT NULL,
    order_id   BIGINT REFERENCES orders (id),
    product_id BIGINT REFERENCES product (id)
);