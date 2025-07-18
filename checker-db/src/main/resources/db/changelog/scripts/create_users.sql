CREATE TABLE IF NOT EXISTS checker.users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL,
    is_active BOOL NOT NULL DEFAULT FALSE
);