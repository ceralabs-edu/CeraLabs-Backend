ALTER TABLE conversations
    ADD instance_id UUID;

ALTER TABLE conversations
    ALTER COLUMN instance_id SET NOT NULL;

CREATE INDEX idx_question_asset_qa_entry ON question_assets (qa_entry_id);

ALTER TABLE conversations
    ADD CONSTRAINT FK_CONVERSATION_AI_INSTANCE FOREIGN KEY (instance_id) REFERENCES ai_packages_instances (id);

CREATE INDEX idx_conversations_instance_id ON conversations (instance_id);