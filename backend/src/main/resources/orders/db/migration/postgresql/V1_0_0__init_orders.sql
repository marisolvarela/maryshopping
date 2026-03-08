
CREATE TABLE IF NOT EXISTS orders.purchase_order
(
    id                   UUID PRIMARY KEY,
    customer_id          UUID         NOT NULL,
    order_date           BIGINT       NOT NULL,
    status               VARCHAR(50) NOT NULL,

    -- Shipping address fields
    shipping_street      VARCHAR(255) NOT NULL,
    shipping_city        VARCHAR(255) NOT NULL,
    shipping_province    VARCHAR(255) NOT NULL,
    shipping_postal_code VARCHAR(255) NOT NULL,
    shipping_country     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders.order_line
(
    order_line_id        UUID PRIMARY KEY,
    order_id             UUID           NOT NULL REFERENCES orders.purchase_order (id) ON DELETE CASCADE,
    product_id           UUID           NOT NULL,
    product_name         VARCHAR(255)   NOT NULL,
    product_provider_reference VARCHAR(255)   NOT NULL,
    category_name        VARCHAR(255)   NOT NULL,
    provider_name        VARCHAR(255)   NOT NULL,
    quantity             INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price           NUMERIC(15, 2) NOT NULL CHECK (unit_price >= 0)
);
