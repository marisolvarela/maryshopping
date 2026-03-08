CREATE TABLE IF NOT EXISTS customers.customer
(
    id                           uuid primary key,
    first_name                   text,
    last_name                    text,
    dni                          text unique,
    phone_number                 text,
    email_address                text unique,

    billing_address_street       text,
    billing_address_city         text,
    billing_address_province     text,
    billing_address_postal_code  text,
    billing_address_country      text,

    shipping_address_street      text,
    shipping_address_city        text,
    shipping_address_province    text,
    shipping_address_postal_code text,
    shipping_address_country     text
);