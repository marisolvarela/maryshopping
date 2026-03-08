
-- Category table
CREATE TABLE IF NOT EXISTS catalog.category (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- Provider table
CREATE TABLE IF NOT EXISTS catalog.provider (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- Product table
CREATE TABLE IF NOT EXISTS catalog.product (
    id UUID PRIMARY KEY,
    category_id UUID NOT NULL REFERENCES catalog.category(id) ON DELETE CASCADE,
    provider_id UUID REFERENCES catalog.provider(id) ON DELETE CASCADE,
    product_provider_ref TEXT,
    name TEXT NOT NULL,
    description TEXT,
    price NUMERIC NOT NULL
);

-- Product image table
CREATE TABLE IF NOT EXISTS catalog.product_image (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES catalog.product(id) ON DELETE CASCADE,
    image_mime_type TEXT NOT NULL,
    image_order INTEGER DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    image_bytes BYTEA NOT NULL
);



