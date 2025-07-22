CREATE TABLE IF NOT EXISTS checker_bot.users(
    telegram_id BIGINT PRIMARY KEY,
    checker_user_id UUID NOT NULL
);