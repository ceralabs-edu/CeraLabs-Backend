-- Add status column to ai_packages
ALTER TABLE ai_packages
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Add status column to ai_packages_instances
ALTER TABLE ai_packages_instances
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

