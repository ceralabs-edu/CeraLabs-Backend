-- Add ON UPDATE CASCADE to question_assets.qa_entry_id foreign key
ALTER TABLE question_assets
DROP CONSTRAINT IF EXISTS fk_question_asset_qa_entry;
ALTER TABLE question_assets
ADD CONSTRAINT fk_question_asset_qa_entry
FOREIGN KEY (qa_entry_id)
REFERENCES qa_entries(id)
ON UPDATE CASCADE;
-- End of migration

