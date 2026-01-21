--liquibase formatted sql

--changeset brayan:04-create-password-reset-tokens

CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_reset_token_user ON password_reset_tokens(user_id);
CREATE INDEX idx_reset_token_code ON password_reset_tokens(code);