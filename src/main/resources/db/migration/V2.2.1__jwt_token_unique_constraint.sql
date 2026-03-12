-- Add unique constraint to jwt_access_tokens.token
ALTER TABLE jwt_access_tokens
    ADD CONSTRAINT uq_jwt_access_tokens_token UNIQUE (token);

