-- Create table for JWT access tokens
CREATE TABLE jwt_access_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    token TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    issued_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    CONSTRAINT fk_jwt_token_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_jwt_access_tokens_user_id ON jwt_access_tokens(user_id);
CREATE INDEX idx_jwt_access_tokens_token ON jwt_access_tokens(token);

