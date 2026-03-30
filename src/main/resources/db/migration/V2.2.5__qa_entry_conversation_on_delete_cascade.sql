-- Add ON DELETE CASCADE to qa_entries.conversation_id foreign key
ALTER TABLE qa_entries
DROP CONSTRAINT IF EXISTS fk_qa_entries_on_conversation;
ALTER TABLE qa_entries
ADD CONSTRAINT fk_qa_entries_on_conversation
FOREIGN KEY (conversation_id)
REFERENCES conversations(id)
ON DELETE CASCADE;
-- End of migration

