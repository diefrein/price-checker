CREATE TABLE IF NOT EXISTS checker.products(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    link TEXT NOT NULL,
    name TEXT NOT NULL,
    actual_price NUMERIC NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);