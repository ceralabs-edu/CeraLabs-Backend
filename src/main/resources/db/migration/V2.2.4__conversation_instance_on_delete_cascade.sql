-- Add ON DELETE CASCADE to conversations.instance_id foreign key
ALTER TABLE conversations
DROP CONSTRAINT IF EXISTS fk_conversation_ai_instance;
ALTER TABLE conversations
ADD CONSTRAINT fk_conversation_ai_instance
FOREIGN KEY (instance_id)
REFERENCES ai_packages_instances(id)
ON DELETE CASCADE ON UPDATE CASCADE;
-- End of migration

