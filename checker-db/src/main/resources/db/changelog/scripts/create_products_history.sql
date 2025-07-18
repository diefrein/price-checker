CREATE TABLE IF NOT EXISTS checker.products_history(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    price NUMERIC NOT NULL,
    price_at_date_time TIMESTAMP NOT NULL DEFAULT NOW()
);