
CREATE TABLE IF NOT EXISTS reports.order_report
(
    -- Order
    order_id                     UUID           NOT NULL,
    order_date                   BIGINT         NOT NULL,

    -- Customer
    customer_id                  UUID           NOT NULL,
    customer_first_name          VARCHAR(255)   NOT NULL,
    customer_last_name           VARCHAR(255)   NOT NULL,
    customer_dni                 VARCHAR(255)   NOT NULL,

    -- Shipping address
    shipping_street              VARCHAR(255)   NOT NULL,
    shipping_city                VARCHAR(255)   NOT NULL,
    shipping_province            VARCHAR(255)   NOT NULL,
    shipping_postal_code         VARCHAR(255)   NOT NULL,
    shipping_country             VARCHAR(255)   NOT NULL,

    -- Order line (1 row per order line)
    order_line_id                UUID           NOT NULL,
    product_id                   UUID           NOT NULL,
    product_name                 VARCHAR(255)   NOT NULL,
    product_provider_reference   VARCHAR(255)   NOT NULL,
    category_name                VARCHAR(255)   NOT NULL,
    provider_name                VARCHAR(255)   NOT NULL,
    quantity                     INTEGER        NOT NULL,
    unit_price                   NUMERIC(15,2) NOT NULL,
    sub_total                    NUMERIC(15,2) NOT NULL,

    -- Totals
    total_amount_due             NUMERIC(15,2) NOT NULL,

    -- Primary key: by order line because it is not repeated.
    CONSTRAINT pk_order_report PRIMARY KEY (order_line_id)
);


