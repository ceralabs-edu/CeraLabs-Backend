ALTER TABLE conversations
    ADD user_id BIGINT;

ALTER TABLE conversations
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE conversations
    ADD CONSTRAINT FK_CONVERSATION_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_conversations_user_id ON conversations (user_id);